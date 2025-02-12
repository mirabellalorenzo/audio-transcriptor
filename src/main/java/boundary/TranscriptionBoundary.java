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
        if (title == null || title.isBlank()) {
            logger.warn("Saving canceled by the user (missing title).");
            return false;
        }

        boolean saved = false;
        File file = null;

        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            saved = controller.saveTranscription(title, null);
            if (saved) {
                logger.info("Transcription saved to Firebase database with title: {}", title);
            } else {
                logger.error("Error saving transcription to Firebase database.");
            }
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Transcription");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            fileChooser.setInitialFileName(title + ".txt");

            // Set default directory to user's home
            String homePath = System.getProperty("user.home");
            File homeDir = new File(homePath);
            if (homeDir.exists() && homeDir.isDirectory()) {
                fileChooser.setInitialDirectory(homeDir);
            }

            file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                saved = controller.saveTranscription(title, file.getAbsolutePath());
                if (saved) {
                    logger.info("Transcription saved at: {}", file.getAbsolutePath());
                } else {
                    logger.error("Error saving the transcription.");
                }
            } else {
                logger.warn("Saving canceled by the user.");
            }
        } else {
            logger.error("Unsupported storage mode.");
        }

        return saved;
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
