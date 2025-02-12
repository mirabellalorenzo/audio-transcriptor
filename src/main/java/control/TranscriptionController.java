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

    public boolean saveTranscription(String title, String filePath) {
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            if (filePath == null) {
                logger.error("Error: Missing file path for File System storage.");
                return false;
            }
            return saveTranscriptionToFile(filePath);
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            return saveTranscriptionToFirebase(title);
        } else {
            logger.error("Error: Unsupported storage mode.");
            return false;
        }
    }    

    private boolean saveTranscriptionToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(transcription.getText());
            writer.flush();
            logger.info("Transcription saved in File System at: {}", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error saving transcription to File System: {}", e.getMessage(), e);
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
