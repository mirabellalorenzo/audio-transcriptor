package boundary;

import control.TranscriptionController;


public class TranscriptionBoundary {
    private TranscriptionController controller;

    public TranscriptionBoundary(TranscriptionController controller) {
        this.controller = controller;
    }

    public boolean uploadAudio(String filePath) {
        boolean success = controller.processAudio(filePath);
        if (success) {
            System.out.println("Trascrizione completata!");
            System.out.println(controller.getTranscription().getText());
        } else {
            System.out.println("Errore nel caricamento del file audio.");
        }
        return success;
    }

    public void saveTranscription(String filePath) {
        boolean saved = controller.saveTranscription(filePath);
        if (saved) {
            System.out.println("Trascrizione salvata con successo!");
        } else {
            System.out.println("Errore nel salvataggio della trascrizione.");
        }
    }
}
