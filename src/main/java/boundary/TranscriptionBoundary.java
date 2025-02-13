package boundary;

import control.TranscriptionController;
import entity.Transcription;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import view.TranscriptionView;
import java.io.File;
import config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranscriptionBoundary {
    private final TranscriptionController controller;
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionBoundary.class);

    public TranscriptionBoundary(TranscriptionController controller) {
        this.controller = controller;
    }

    public boolean uploadAudio(String filePath) {
        boolean success = controller.processAudio(filePath);
        if (success) {
            logger.info("Transcription completed successfully.");
            logger.info("Transcription text: {}", controller.getTranscription().getText());
        } else {
            logger.error("Error uploading the audio file.");
        }
        return success;
    }

    public boolean saveTranscription(Stage primaryStage) {
        TextInputDialog titleDialog = new TextInputDialog("New Note");
        titleDialog.setTitle("Enter Title");
        titleDialog.setHeaderText("Create a new note");
        titleDialog.setContentText("Title:");
    
        String title = titleDialog.showAndWait().orElse(null);
        if (title == null || title.isBlank()) return false;
    
        boolean saved = false;
    
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            saved = controller.saveTranscription(title, null);
            logger.info("Transcription {} the Firebase database with title: {}", saved ? "saved to" : "failed in", title);
    
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Transcription");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName(title + ".txt");
    
            File homeDir = new File(System.getProperty("user.home"));
            if (homeDir.isDirectory()) fileChooser.setInitialDirectory(homeDir);
    
            File file = fileChooser.showSaveDialog(primaryStage);
            saved = (file != null) && controller.saveTranscription(title, file.getAbsolutePath());
    
            logger.info("Transcription {} at: {}", saved ? "saved" : "failed", file != null ? file.getAbsolutePath() : "No file selected");
        } else {
            logger.error("Unsupported storage mode.");
        }
    
        return saved;
    }    

    public void updateTranscription(Transcription transcription) {
        this.controller.setTranscription(transcription);
    }    

    public void openTranscriptionView(Stage primaryStage) {
        logger.info("Opening Transcription View.");
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public Transcription getTranscription() {
        return controller.getTranscription();
    }
}
