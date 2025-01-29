package control;

import util.AppConfig;
import entity.Transcription;
import entity.Note;
import entity.User;
import persistence.FirebaseNotesDAO;

import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TranscriptionController {
    private final FirebaseNotesDAO notesDAO = new FirebaseNotesDAO(); // Iniezione del DAO
    private Transcription transcription;

    public boolean processAudio(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.canRead()) {
            System.err.println("Errore: il file audio non esiste o non può essere letto.");
            return false;
        }
    
        long startTime = System.currentTimeMillis(); // Inizio del timer
    
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
    
            long endTime = System.currentTimeMillis(); // Fine del timer
            long processingTime = endTime - startTime; // Calcolo tempo impiegato
    
            transcription = new Transcription(result.toString().trim(), 120, System.currentTimeMillis(), processingTime);
            System.out.println("Trascrizione completata con successo in " + processingTime + " ms.");
            return true;
    
        } catch (IOException e) {
            System.err.println("Errore durante la trascrizione: " + e.getMessage());
            return false;
        }
    }    

    public boolean saveTranscription(String filePath) {
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            if (filePath == null) {
                System.err.println("Errore: file path mancante per il salvataggio in modalità File System.");
                return false;
            }
            return saveTranscriptionToFile(filePath);
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            // Usa un titolo predefinito se il titolo non è fornito
            String defaultTitle = "Trascrizione " + System.currentTimeMillis();
            return saveTranscriptionToFirebase(defaultTitle);
        } else {
            System.err.println("Errore: modalità di archiviazione non supportata.");
            return false;
        }
    }    

    private boolean saveTranscriptionToFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(transcription.getText());
            writer.flush();
            System.out.println("Trascrizione salvata in modalità File System in: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio in modalità File System: " + e.getMessage());
            return false;
        }
    }

    public boolean saveTranscriptionToFirebase(String title) {
        User user = AuthController.getCurrentUser(); 
        if (user == null) {
            System.err.println("Errore: utente non autenticato.");
            return false;
        }
    
        Note note = new Note(
            transcription.getId(),    // id
            user.getId(),             // uid
            title,                    // title
            transcription.getText()   // content
        );
    
        try {
            notesDAO.save(note);
            System.out.println("Nota salvata nel database Firebase con successo!");
            return true;
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio della nota nel database Firebase.");
            e.printStackTrace();
            return false;
        }
    }    

    public Transcription getTranscription() {
        return transcription;
    }
}
