package boundary;

import java.io.FileWriter;
import java.io.IOException;

public class TranscriptionOutputBoundary {

    public void saveTranscriptionToFile(String filePath, String transcriptionText) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(transcriptionText);
            System.out.println("Transcription saved to file: " + filePath);
        } catch (IOException e) {
            System.out.println("Failed to save transcription: " + e.getMessage());
        }
    }

    public void displayTranscription(String transcriptionText) {
        System.out.println("Transcription:");
        System.out.println(transcriptionText);
    }
}
