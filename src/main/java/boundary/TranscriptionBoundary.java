package boundary;

import control.TranscriptionController;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.AppConfig;

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
        TextInputDialog titleDialog = new TextInputDialog("Nuova Nota");
        titleDialog.setTitle("Inserisci Titolo");
        titleDialog.setHeaderText("Crea una nuova nota");
        titleDialog.setContentText("Titolo:");

        String title = titleDialog.showAndWait().orElse(null);
        if (title == null || title.isBlank()) {
            System.out.println("Salvataggio annullato dall'utente (titolo mancante).");
            return;
        }

        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            boolean saved = controller.saveTranscriptionToFirebase(title);
            if (saved) {
                System.out.println("Trascrizione salvata nel database Firebase con titolo: " + title);
            } else {
                System.err.println("Errore durante il salvataggio nel database Firebase.");
            }
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Transcription");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File di Testo", "*.txt"));

            fileChooser.setInitialFileName(title + ".txt");
            String homePath = System.getProperty("user.home");
            File homeDir = new File(homePath);
            if (homeDir.exists() && homeDir.isDirectory()) {
                fileChooser.setInitialDirectory(homeDir);
            }

            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                boolean saved = controller.saveTranscription(file.getAbsolutePath());
                if (saved) {
                    System.out.println("Trascrizione salvata in: " + file.getAbsolutePath());
                } else {
                    System.err.println("Errore durante il salvataggio della trascrizione.");
                }
            } else {
                System.out.println("Salvataggio annullato dall'utente.");
            }
        } else {
            System.err.println("Modalità di archiviazione non supportata.");
        }
    }
}
