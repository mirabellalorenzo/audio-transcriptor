package control;

import entity.Transcription;
import entity.Note;
import util.AudioConverter;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfig;
import java.util.function.DoubleConsumer;

public class TranscriptionController {
    private final NotesDAO notesDAO;
    private Transcription transcription;

    public TranscriptionController(AppConfig appConfig) {
        if (appConfig == null) {
            throw new IllegalArgumentException("AppConfig cannot be null");
        }
        this.notesDAO = NotesDAOFactory.getNotesDAO(appConfig);
    }


    public TranscriptionBean processAudio(TranscriptionBean transcriptionBean, DoubleConsumer progressCallback) {
        File originalFile = new File(transcriptionBean.getFilePath());
        if (!originalFile.exists() || !originalFile.canRead()) {
            throw new IllegalArgumentException("The audio file does not exist or cannot be read.");
        }

        // Usare la classe AudioConverter
        File convertedFile;
        try {
            convertedFile = AudioConverter.convertToWav16KHzMono(originalFile);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException("Audio conversion failed.", e);
        }

        long startTime = System.currentTimeMillis();

        try (Model model = new Model("src/main/resources/models/vosk-model-small-it-0.22");
             FileInputStream audioStream = new FileInputStream(convertedFile);
             Recognizer recognizer = new Recognizer(model, 16000)) {

            byte[] buffer = new byte[16384];
            long totalBytes = convertedFile.length();
            long processedBytes = 0;
            int bytesRead;
            StringBuilder result = new StringBuilder();

            while ((bytesRead = audioStream.read(buffer)) != -1) {
                processedBytes += bytesRead;
                double progress = (double) processedBytes / totalBytes;
                progressCallback.accept(progress);

                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    JsonObject jsonResult = JsonParser.parseString(recognizer.getResult()).getAsJsonObject();
                    result.append(jsonResult.get("text").getAsString()).append(" ");
                }
            }

            JsonObject finalResult = JsonParser.parseString(recognizer.getFinalResult()).getAsJsonObject();
            result.append(finalResult.get("text").getAsString());

            long processingTime = System.currentTimeMillis() - startTime;

            // Eliminare il file convertito se non è il file originale
            if (!convertedFile.equals(originalFile) && convertedFile.exists()) {
                convertedFile.delete();
            }

            return new TranscriptionBean(
                    "",
                    result.toString().trim(),
                    transcriptionBean.getDuration(),
                    transcriptionBean.getCreatedAt(),
                    processingTime
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during transcription", e);
        }
    }

    public boolean saveTranscription(TranscriptionBean transcriptionBean) {
        if (transcriptionBean == null || transcriptionBean.getTitle() == null || transcriptionBean.getTitle().isBlank() || transcriptionBean.getText() == null) {
            throw new IllegalArgumentException("Invalid transcription data.");
        }

        UserBean user = AuthController.getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("No authenticated user.");
        }

        Note note = new Note(
                transcriptionBean.getText(),
                user.getId(),
                transcriptionBean.getTitle().replaceAll("[^a-zA-Z0-9]", "_"),
                transcriptionBean.getText()
        );

        try {
            notesDAO.save(note);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error saving transcription", e);
        }
    }

    public TranscriptionBean getTranscription() {
        if (transcription == null) {
            return null;
        }
        return new TranscriptionBean(
                transcription.getText(),
                transcription.getDuration(),
                transcription.getCreatedAt(),
                transcription.getProcessingTime()
        );
    }

    public void setTranscription(TranscriptionBean transcriptionBean) {
        this.transcription = new Transcription(
                transcriptionBean.getText(),
                transcriptionBean.getDuration(),
                transcriptionBean.getCreatedAt(),
                transcriptionBean.getProcessingTime()
        );
    }
}
