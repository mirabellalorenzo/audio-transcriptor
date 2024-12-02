package persistence;

import java.util.HashMap;
import java.util.Map;

import entity.Transcription;

public class InMemoryTranscriptionDao implements TranscriptionDao {

    private static InMemoryTranscriptionDao instance = new InMemoryTranscriptionDao();

    public static InMemoryTranscriptionDao getInstance() {
        return instance;
    }

    private Map<String, Transcription> transcriptions = new HashMap<>();

    @Override
    public void storeTranscription(Transcription transcription) {
        transcriptions.put(transcription.getFilePath(), transcription);
    }

    @Override
    public Transcription loadTranscription(String filePath) {
        return transcriptions.get(filePath);
    }
}
