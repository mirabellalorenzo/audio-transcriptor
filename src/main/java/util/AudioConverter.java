package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class AudioConverter {

    public static File convertToWav16KHzMono(File inputFile) throws IOException, InterruptedException {
        if (inputFile.getName().endsWith(".wav") && isWavCompatible(inputFile)) {
            return inputFile;
        }

        File tempDir = new File(System.getProperty("java.io.tmpdir"), "audio_transcriptor_secure");
        if (!tempDir.exists() && !tempDir.mkdir()) {
            throw new IllegalStateException("Failed to create temporary directory.");
        }

        File outputFile = new File(tempDir, inputFile.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_converted.wav");
        outputFile.deleteOnExit();

        String command = String.format("ffmpeg -i \"%s\" -ar 16000 -ac 1 \"%s\" -y", inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        process.waitFor();

        if (process.exitValue() != 0) {
            throw new IOException("Audio conversion failed.");
        }

        return outputFile;
    }

    private static boolean isWavCompatible(File wavFile) throws IOException, InterruptedException {
        String command = String.format("ffprobe -i \"%s\" -show_entries stream=sample_rate,channels -of csv=p=0", wavFile.getAbsolutePath());
        ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = reader.readLine();
        process.waitFor();

        if (result != null) {
            String[] parts = result.split(",");
            return parts.length == 2 && Integer.parseInt(parts[0].trim()) == 16000 && Integer.parseInt(parts[1].trim()) == 1;
        }
        return false;
    }
}
