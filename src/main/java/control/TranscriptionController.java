package control;

import entity.Transcription;
import entity.Note;
import entity.User;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranscriptionController {
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
    private final NotesDAO notesDAO = NotesDAOFactory.getNotesDAO();
    private Transcription transcription;

    public boolean processAudio(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.canRead()) {
            logger.error("Error: The audio file does not exist or cannot be read.");
            return false;
        }

        long startTime = System.currentTimeMillis();

        try (Model model = new Model("src/main/resources/models/vosk-model-small-it-0.22");
             FileInputStream audioStream = new FileInputStream(audioFile);
             Recognizer recognizer = new Recognizer(model, 16000)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            StringBuilder result = new StringBuilder();

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    JsonObject jsonResult = JsonParser.parseString(recognizer.getResult()).getAsJsonObject();
                    result.append(jsonResult.get("text").getAsString()).append(" ");
                }
            }

            JsonObject finalResult = JsonParser.parseString(recognizer.getFinalResult()).getAsJsonObject();
            result.append(finalResult.get("text").getAsString());

            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            transcription = new Transcription(result.toString().trim(), 120, System.currentTimeMillis(), processingTime);
            logger.info("Transcription completed successfully in {} ms.", processingTime);
            return true;

        } catch (IOException e) {
            logger.error("Error during transcription: {}", e.getMessage(), e);
            return false;
        }
    }    

    public boolean saveTranscription(String title) {
        if (title == null || title.isBlank() || transcription == null) {
            logger.error("Invalid transcription or title.");
            return false;
        }
    
        boolean saved = false;
    
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            saved = saveTranscriptionToFirebase(title);
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            saved = saveTranscriptionToFile(title, transcription.getId());
        } else {
            logger.error("Unsupported storage mode.");
        }
    
        return saved;
    }           

    public void setTranscription(Transcription transcription) {
        this.transcription = transcription;
    }    

    private boolean saveTranscriptionToFile(String title, String id) {
        User user = AuthController.getCurrentUser();
        if (user == null) {
            logger.error("Error: No authenticated user.");
            return false;
        }
    
        String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");
    
        // Creiamo una nuova nota con ID, utente, titolo e testo della trascrizione
        Note note = new Note(id, user.getId(), safeTitle, transcription.getText());
    
        try {
            // Salviamo la nota usando il DAO, che gestirà tutto correttamente
            notesDAO.save(note);
            logger.info("Transcription saved as a note: {}", note.getTitle());
            return true;
        } catch (IOException e) {
            logger.error("Error saving transcription to file: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean saveTranscriptionToFirebase(String title) {
        User user = AuthController.getCurrentUser();
        if (user == null) {
            logger.error("Error: No authenticated user.");
            return false;
        }

        Note note = new Note(
            transcription.getId(),
            user.getId(),
            title,
            transcription.getText()
        );

        try {
            notesDAO.save(note);
            logger.info("Note successfully saved in Firebase with title: {}", title);
            return true;
        } catch (Exception e) {
            logger.error("Error saving note in Firebase: {}", e.getMessage(), e);
            return false;
        }
    }    

    public Transcription getTranscription() {
        return transcription;
    }
}
