package boundary;

import control.TranscriptionController;
import control.TranscriptionBean;
import java.util.function.DoubleConsumer;
import javafx.concurrent.Task;

public class TranscriptionBoundary {
    private final TranscriptionController controller;

    public TranscriptionBoundary(TranscriptionController controller) {
        this.controller = controller;
    }

    public void uploadAudioAsync(TranscriptionBean transcriptionBean, DoubleConsumer progressCallback, Runnable onSuccess, Runnable onFailure) {
        if (transcriptionBean == null || transcriptionBean.getFilePath() == null || transcriptionBean.getFilePath().isBlank()) {
            throw new IllegalArgumentException("Invalid transcription data: file path is missing.");
        }

        Task<TranscriptionBean> task = new Task<>() {
            @Override
            protected TranscriptionBean call() {
                return controller.processAudio(transcriptionBean, progressCallback);
            }
        };

        task.setOnSucceeded(event -> {
            TranscriptionBean result = task.getValue();
            if (result == null || result.getText() == null || result.getText().isBlank()) {
                throw new IllegalStateException("Transcription process completed but no valid transcription was generated.");
            }

            if (result.getText() != null && !result.getText().isBlank()) {
                controller.setTranscription(result);
                onSuccess.run();
            } else {
                onFailure.run();
            }
        });

        task.setOnFailed(event -> {
            throw new IllegalStateException("Audio transcription task failed.", task.getException());
        });

        new Thread(task).start();
    }
}
