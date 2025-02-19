package control;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranscriptionControllerTest {
    private TranscriptionController transcriptionController;

    @BeforeEach
    void setUp() {
        transcriptionController = new TranscriptionController();
    }

    @Test
    void testProcessAudio_ValidFile() {
        boolean result = transcriptionController.processAudio("src/test/resources/test_audio.wav", progress -> {
            System.out.println("Progresso: " + (progress * 100) + "%");
        });

        System.out.println("Risultato del test: " + result);
        assertTrue(result, "Il file audio dovrebbe essere elaborato correttamente.");
    }    

    @Test
    void testProcessAudio_InvalidFile() {
        String filePath = "src/test/resources/non_existing_file.wav";
        boolean result = transcriptionController.processAudio(filePath, progress -> {
        });

        assertFalse(result, "Il file audio non esistente non dovrebbe essere elaborato.");
    }
}
