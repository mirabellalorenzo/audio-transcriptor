package control;

import entity.Transcription;
import javafx.stage.Stage;
import view.HomeView;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class TranscriptionController {
    private Transcription transcription;

    public boolean processAudio(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists() || !audioFile.canRead()) {
            System.err.println("Errore: il file audio non esiste o non può essere letto.");
            return false;
        }

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

            transcription = new Transcription(result.toString().trim(), 120, System.currentTimeMillis());
            System.out.println("Trascrizione completata con successo.");
            return true;

        } catch (IOException e) {
            System.err.println("Errore durante la trascrizione: " + e.getMessage());
            return false;
        }
    }

    public Transcription getTranscription() {
        return transcription;
    }

    public boolean saveTranscription(String filePath) {
        if (transcription != null) {
            File file = new File(filePath);
    
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(transcription.getText()); // Scrive il testo della trascrizione
                writer.flush();
                System.out.println("Trascrizione salvata in: " + filePath);
                return true;
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio della trascrizione: " + e.getMessage());
                return false;
            }
        } else {
            System.err.println("Errore: nessuna trascrizione disponibile.");
            return false;
        }
    }

    public void openHome(Stage primaryStage) {
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }
}
