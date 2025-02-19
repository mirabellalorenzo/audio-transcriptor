package boundary;

import control.TranscriptionController;
import entity.Transcription;
import javafx.stage.Stage;
import view.gui1.TranscriptionView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.Consumer;
import javafx.concurrent.Task;


public class TranscriptionBoundary {
    private final TranscriptionController controller;
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionBoundary.class);

    public TranscriptionBoundary(TranscriptionController controller) {
        this.controller = controller;
    }

    public void uploadAudioAsync(String filePath, Consumer<Double> progressCallback, Runnable onSuccess, Runnable onFailure) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                return controller.processAudio(filePath, progressCallback);
            }
        };
    
        task.setOnSucceeded(event -> {
            if (task.getValue()) {
                logger.info("Transcription completed successfully.");
                logger.info("Transcription text: {}", controller.getTranscription().getText());
                onSuccess.run();
            } else {
                logger.error("Error uploading the audio file.");
                onFailure.run();
            }
        });
    
        task.setOnFailed(event -> {
            logger.error("Transcription task failed.");
            onFailure.run();
        });
    
        new Thread(task).start();
    }    

    public boolean saveTranscription(String title) {
        if (title == null || title.isBlank()) return false;
    
        boolean saved = controller.saveTranscription(title);
        logger.info("Transcription {} successfully", saved ? "saved" : "failed");
    
        return saved;
    }     

    public void updateTranscription(Transcription transcription) {
        this.controller.setTranscription(transcription);
    }    

    public Transcription getTranscription() {
        return controller.getTranscription();
    }

    public void openTranscriptionView(Stage primaryStage) {
        logger.info("Opening Transcription View.");
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }
}
