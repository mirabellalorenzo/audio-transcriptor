package entity;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TranscriptionTest {
    private Transcription transcription;

    @BeforeEach
    void setUp() {
        transcription = new Transcription("Questo è un test di trascrizione", 120, System.currentTimeMillis(), 500);
    }

    @Test
    void testGetText() {
        assertEquals("Questo è un test di trascrizione", transcription.getText(), "La trascrizione non corrisponde.");
    }

    @Test
    void testWordCount() {
        assertEquals(6, transcription.getWordCount(), "Il conteggio delle parole dovrebbe essere corretto.");
    }
}
