package control;

import entity.Transcription;
import entity.Note;
import entity.User;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import org.vosk.Model;
import org.vosk.Recognizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.function.DoubleConsumer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import org.apache.commons.lang3.SystemUtils;

public class TranscriptionController {
    private static final Logger logger = LoggerFactory.getLogger(TranscriptionController.class);
    private final NotesDAO notesDAO = NotesDAOFactory.getNotesDAO();
    private Transcription transcription;

    public boolean processAudio(String filePath, DoubleConsumer progressCallback) {
        File originalFile = new File(filePath);
        if (!originalFile.exists() || !originalFile.canRead()) {
            logger.error("Error: The audio file does not exist or cannot be read.");
            return false;
        }

        File convertedFile = convertToWav16KHzMono(originalFile);
        if (convertedFile == null) {
            logger.error("Error: Audio conversion failed.");
            return false;
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

            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            transcription = new Transcription(result.toString().trim(), 120, System.currentTimeMillis(), processingTime);
            logger.info("Transcription completed successfully in {} ms.", processingTime);

            if (!convertedFile.equals(originalFile) && convertedFile.exists()) {
                if (!convertedFile.delete()) {
                    logger.warn("Failed to delete temporary file: " + convertedFile.getAbsolutePath());
                } else {
                    logger.info("Temporary file deleted: " + convertedFile.getAbsolutePath());
                }
            }            

            return true;

        } catch (IOException e) {
            logger.error("Error during transcription: {}", e.getMessage(), e);
            return false;
        }
    }

    private File convertToWav16KHzMono(File inputFile) {
        try {
            String inputPath = inputFile.getAbsolutePath();
    
            if (inputPath.endsWith(".wav")) {
                if (isWavCompatible(inputPath)) {
                    logger.info("File WAV already compatible, no conversion needed.");
                    return inputFile;
                } else {
                    logger.info("File WAV not compatible, proceeding with conversion.");
                }
            }
    
            // Creazione di una directory sicura per i file temporanei
            Path tempDir;
            if (SystemUtils.IS_OS_UNIX) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                    PosixFilePermissions.fromString("rwx------")
                );
                tempDir = Files.createTempDirectory("audio_transcriptor_", attr); // Conforme
            } else {
                tempDir = Files.createTempDirectory("audio_transcriptor_"); // Conforme
            }
    
            // Creazione del file temporaneo all'interno della directory sicura
            Path tempFile = Files.createTempFile(tempDir, "converted_", ".wav");
            File outputFile = tempFile.toFile();
            outputFile.deleteOnExit();
    
            // Impostazione dei permessi su Windows
            if (!SystemUtils.IS_OS_UNIX) {
                if (!outputFile.setReadable(true, true)) {
                    logger.warn("Failed to set readable permissions for file: {}", outputFile.getAbsolutePath());
                }
                if (!outputFile.setWritable(true, true)) {
                    logger.warn("Failed to set writable permissions for file: {}", outputFile.getAbsolutePath());
                }
                if (!outputFile.setExecutable(true, true)) {
                    logger.warn("Failed to set executable permissions for file: {}", outputFile.getAbsolutePath());
                }
            }
    
            String outputPath = outputFile.getAbsolutePath();
            String command = String.format("ffmpeg -i \"%s\" -ar 16000 -ac 1 \"%s\" -y", inputPath, outputPath);
    
            logger.info("Running FFmpeg command: {}", command);
    
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder ffmpegOutput = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ffmpegOutput.append(line).append("\n");
            }
    
            int exitCode = process.waitFor();
            logger.info("FFmpeg exit code: {}", exitCode);
    
            if (exitCode != 0) {
                logger.error("FFmpeg conversion failed. Output:\n{}", ffmpegOutput);
                return null;
            }
    
            logger.info("Audio converted successfully to {}", outputPath);
            return outputFile;
    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Audio conversion interrupted", e);
        } catch (IOException e) {
            logger.error("Error during audio conversion: {}", e.getMessage(), e);
            return null;
        }
    }    
    
    private boolean isWavCompatible(String filePath) {
        try {
            String command = String.format("ffprobe -i \"%s\" -show_entries stream=sample_rate,channels -of csv=p=0", filePath);
            
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String result = reader.readLine();
            process.waitFor();
    
            if (result != null) {
                logger.info("FFprobe output: " + result);
                String[] parts = result.split(",");
                if (parts.length == 2) {
                    int sampleRate = Integer.parseInt(parts[0].trim());
                    int channels = Integer.parseInt(parts[1].trim());
    
                    return sampleRate == 16000 && channels == 1;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("WAV compatibility check interrupted: {}", e.getMessage(), e);
            return false;
        } catch (IOException e) {
            logger.error("Error checking WAV compatibility: {}", e.getMessage(), e);
        }        
        return false;
    }    

    public boolean saveTranscription(String title) {
        if (title == null || title.isBlank() || transcription == null) {
            logger.error("Invalid transcription or title.");
            return false;
        }

        boolean saved = false;

        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            saved = saveTranscriptionToFirebase(title);
        } else if (AppConfig.getStorageMode() == AppConfig.StorageMode.FILE_SYSTEM) {
            saved = saveTranscriptionToFile(title, transcription.getId());
        } else {
            logger.error("Unsupported storage mode.");
        }

        return saved;
    }

    public void setTranscription(Transcription transcription) {
        this.transcription = transcription;
    }

    private boolean saveTranscriptionToFile(String title, String id) {
        User user = AuthController.getCurrentUser();
        if (user == null) {
            logger.error("Error: No authenticated user.");
            return false;
        }

        String safeTitle = title.replaceAll("[^a-zA-Z0-9]", "_");

        Note note = new Note(id, user.getId(), safeTitle, transcription.getText());

        try {
            notesDAO.save(note);
            logger.info("Transcription saved as a note: {}", note.getTitle());
            return true;
        } catch (IOException e) {
            logger.error("Error saving transcription to file: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean saveTranscriptionToFirebase(String title) {
        User user = AuthController.getCurrentUser();
        if (user == null) {
            logger.error("Error: No authenticated user.");
            return false;
        }

        Note note = new Note(
            transcription.getId(),
            user.getId(),
            title,
            transcription.getText()
        );

        try {
            notesDAO.save(note);
            logger.info("Note successfully saved in Firebase with title: {}", title);
            return true;
        } catch (Exception e) {
            logger.error("Error saving note in Firebase: {}", e.getMessage(), e);
            return false;
        }
    }

    public Transcription getTranscription() {
        return transcription;
    }
}
