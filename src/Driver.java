import boundary.ConfigurationBoundary;
import boundary.AudioInputBoundary;

public class Driver {
    public static void main(String[] args) {
        
        // Set persistence provider
        ConfigurationBoundary configurationBoundary = new ConfigurationBoundary();
        configurationBoundary.setPersistenceProvider("in memory");

        // Initialize boundary
        AudioInputBoundary audioInputBoundary = new AudioInputBoundary();

        // Load audio file and transcribe
        audioInputBoundary.loadAudioFile("example_audio.wav");
        audioInputBoundary.transcribeAudio();

        // Save the transcription
        audioInputBoundary.saveTranscription("transcription_output.txt");
    }
}
