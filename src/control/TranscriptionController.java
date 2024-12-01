package control;

import entity.Transcription;
import persistence.TranscriptionDao;
import persistence.TranscriptionDaoFactory;

public class TranscriptionController {

    private TranscriptionDao transcriptionDao = TranscriptionDaoFactory.getInstance().getTranscriptionDao();
    private Transcription currentTranscription;

    public void loadAudio(LoadAudioBean bean) {
        if (bean == null || bean.getFilePath() == null || bean.getFilePath().isEmpty()) {
            throw new IllegalArgumentException("Invalid audio file path.");
        }
        // Simula il caricamento di un file audio associandolo a una nuova trascrizione
        currentTranscription = new Transcription(bean.getFilePath());
    }

    public void transcribeAudio() {
        if (currentTranscription == null) {
            throw new IllegalStateException("No audio file loaded to transcribe.");
        }
        // Simula il processo di trascrizione
        String dummyTranscription = "This is a dummy transcription for file: " + currentTranscription.getFilePath();
        currentTranscription.setText(dummyTranscription);
    }

    public void saveTranscription(SaveTranscriptionBean bean) {
        if (currentTranscription == null || currentTranscription.getText() == null) {
            throw new IllegalStateException("No transcription available to save.");
        }
        transcriptionDao.storeTranscription(currentTranscription);
    }

    public String getTranscriptionText() {
        if (currentTranscription == null || currentTranscription.getText() == null) {
            throw new IllegalStateException("No transcription available.");
        }
        return currentTranscription.getText();
    }
}
