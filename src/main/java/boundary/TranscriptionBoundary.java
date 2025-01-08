package boundary;

import control.TranscriptionController;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;


public class TranscriptionBoundary {
    private TranscriptionController controller;

    public TranscriptionBoundary(TranscriptionController controller) {
        this.controller = controller;
    }

    public boolean uploadAudio(String filePath) {
        boolean success = controller.processAudio(filePath);
        if (success) {
            System.out.println("Trascrizione completata!");
            System.out.println(controller.getTranscription().getText());
        } else {
            System.out.println("Errore nel caricamento del file audio.");
        }
        return success;
    }

    public void saveTranscription(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Transcription");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File di Testo", "*.txt"));
    
        fileChooser.setInitialFileName("transcription.txt");
    
        String homePath = System.getProperty("user.home");
        File homeDir = new File(homePath);
        if (homeDir.exists() && homeDir.isDirectory()) {
            fileChooser.setInitialDirectory(homeDir);
        }
    
        File file = fileChooser.showSaveDialog(primaryStage);
    
        if (file != null) {
            String filePath = file.getAbsolutePath();
            if (!filePath.endsWith(".txt")) {
                filePath += ".txt";
            }
    
            boolean saved = controller.saveTranscription(filePath);
            if (saved) {
                System.out.println("Trascrizione salvata in: " + filePath);
            } else {
                System.err.println("Errore durante il salvataggio della trascrizione.");
            }
        } else {
            System.out.println("Salvataggio annullato dall'utente.");
        }
    }
}
