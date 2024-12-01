package persistence;

import entity.Transcription;

public interface TranscriptionDao {

    void storeTranscription(Transcription transcription);

    Transcription loadTranscription(String filePath);
}
