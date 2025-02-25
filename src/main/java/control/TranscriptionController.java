package control;

import entity.Transcription;
import entity.Note;
import entity.User;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfig;
import java.util.function.DoubleConsumer;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.lang3.SystemUtils;

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

        File convertedFile = convertToWav16KHzMono(originalFile);
        if (convertedFile == null) {
            throw new IllegalStateException("Audio conversion failed.");
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

            if (!convertedFile.equals(originalFile) && convertedFile.exists()) {
                convertedFile.delete();
            }

            return new TranscriptionBean(
                    result.toString().trim(),
                    transcriptionBean.getDuration(),
                    transcriptionBean.getCreatedAt(),
                    processingTime
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during transcription", e);
        }
    }

    private File convertToWav16KHzMono(File inputFile) {
        try {
            String inputPath = inputFile.getAbsolutePath();
            if (inputPath.endsWith(".wav") && isWavCompatible(inputPath)) {
                return inputFile;
            }

            File secureTempDir = createSecureTempDirectory();
            File outputFile = createTempFile(secureTempDir);
            setFilePermissions(outputFile);

            if (!convertAudio(inputPath, outputFile.getAbsolutePath())) {
                return null;
            }
            return outputFile;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Audio conversion interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Error during audio conversion", e);
        }
    }

    private File createSecureTempDirectory() {
        File secureTempDir = new File(System.getProperty("java.io.tmpdir"), "audio_transcriptor_secure");
        if (!secureTempDir.exists() && !secureTempDir.mkdir()) {
            throw new IllegalStateException("Failed to create secure temporary directory.");
        }
        return secureTempDir;
    }

    private File createTempFile(File tempDir) {
        try {
            Path tempFile = Files.createTempFile(tempDir.toPath(), "converted_", ".wav");
            File outputFile = tempFile.toFile();
            outputFile.deleteOnExit();
            return outputFile;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp file", e);
        }
    }

    private void setFilePermissions(File file) {
        if (!SystemUtils.IS_OS_UNIX) {
            file.setReadable(true, true);
            file.setWritable(true, true);
            file.setExecutable(true, true);
        }
    }

    private boolean convertAudio(String inputPath, String outputPath) throws IOException, InterruptedException {
        String command = String.format("ffmpeg -i \"%s\" -ar 16000 -ac 1 \"%s\" -y", inputPath, outputPath);
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();
        return process.exitValue() == 0;
    }

    private boolean isWavCompatible(String filePath) {
        try {
            String command = String.format("ffprobe -i \"%s\" -show_entries stream=sample_rate,channels -of csv=p=0", filePath);
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            process.waitFor();

            if (result != null) {
                String[] parts = result.split(",");
                return parts.length == 2 && Integer.parseInt(parts[0].trim()) == 16000 && Integer.parseInt(parts[1].trim()) == 1;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("WAV compatibility check interrupted", e);
        } catch (IOException e) {
            throw new RuntimeException("Error checking WAV compatibility", e);
        }
        return false;
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
