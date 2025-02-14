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

    public boolean saveTranscription(Stage primaryStage, String title) {
        if (title == null || title.isBlank()) return false;
    
        boolean saved = controller.saveTranscription(title);
        logger.info("Transcription {} successfully", saved ? "saved" : "failed");
    
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
