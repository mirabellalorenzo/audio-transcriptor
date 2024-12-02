package control;

import org.vosk.Model;
import org.vosk.Recognizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;

public class SpeechToTextController {

    private static final String MODEL_PATH = "src/main/resources/models/vosk-model-small-it-0.22";

    public static String transcribe(String filePath) {
        StringBuilder transcription = new StringBuilder();

        try {
            // Converti il file in WAV
            File wavFile = convertToWav(filePath);

            if (wavFile == null) {
                return "Errore durante la conversione del file audio in WAV.";
            }

            // Inizializza il modello e il riconoscitore
            try (Model model = new Model(MODEL_PATH);
                 Recognizer recognizer = new Recognizer(model, 16000);
                 InputStream ais = new FileInputStream(wavFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = ais.read(buffer)) != -1) {
                    if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                        transcription.append(recognizer.getResult()).append("\n");
                    }
                }
                transcription.append(recognizer.getFinalResult());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Errore durante la trascrizione: " + e.getMessage();
        }

        return transcription.toString();
    }

    private static File convertToWav(String filePath) {
        try {
            File inputFile = new File(filePath);
            File outputFile = new File("converted_audio.wav");

            ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", inputFile.getAbsolutePath(),
                "-ar", "16000",
                "-ac", "1",
                outputFile.getAbsolutePath()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            process.waitFor();

            if (process.exitValue() == 0 && outputFile.exists()) {
                return outputFile;
            } else {
                System.err.println("Errore durante la conversione audio con ffmpeg.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
