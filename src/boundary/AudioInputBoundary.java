package boundary;

import control.AudioTranscriptionController;
import control.LoadAudioBean;
import control.SaveTranscriptionBean;

public class AudioInputBoundary {

    private AudioTranscriptionController transcriptionController = new AudioTranscriptionController();
    private TranscriptionOutputBoundary outputBoundary = new TranscriptionOutputBoundary();

    public void loadAudioFile(String filePath) {
        try {
            LoadAudioBean audioBean = new LoadAudioBean(filePath);
            transcriptionController.loadAudio(audioBean);
            System.out.println("Audio file loaded: " + filePath);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to load audio file: " + e.getMessage());
        }
    }

    public void transcribeAudio() {
        try {
            transcriptionController.transcribeAudio();
            System.out.println("Audio transcribed successfully.");
            // Display the transcription
            String transcriptionText = transcriptionController.getTranscriptionText();
            outputBoundary.displayTranscription(transcriptionText);
        } catch (Exception e) {
            System.out.println("Failed to transcribe audio: " + e.getMessage());
        }
    }

    public void saveTranscription(String filePath) {
        try {
            SaveTranscriptionBean saveBean = new SaveTranscriptionBean(filePath);
            transcriptionController.saveTranscription(saveBean);
            // Save transcription to file
            String transcriptionText = transcriptionController.getTranscriptionText();
            outputBoundary.saveTranscriptionToFile(filePath, transcriptionText);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to save transcription: " + e.getMessage());
        }
    }
}
