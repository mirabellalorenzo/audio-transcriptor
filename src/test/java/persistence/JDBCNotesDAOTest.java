package persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import entity.Note;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

class JDBCNotesDAOTest {
    private JDBCNotesDAO jdbcNotesDAO;
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    @BeforeEach
    void setUp() {
        jdbcNotesDAO = new JDBCNotesDAO();
        resetDatabase();
    }

    private void resetDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM notes"); // Pulisce il database prima di ogni test
        } catch (Exception e) {
            throw new RuntimeException("Errore nella pulizia del database per i test", e);
        }
    }

    @Test
    void testSaveNote() {
        Note note = new Note(null, "testUser", "Nota di test", "Questo è il contenuto della nota");
        assertDoesNotThrow(() -> jdbcNotesDAO.save(note), "Il salvataggio su MariaDB non dovrebbe generare errori.");
    }

    @Test
    void testRetrieveNotes() {
        Note note = new Note(null, "testUser", "Nota di test", "Questo è il contenuto della nota");
        jdbcNotesDAO.save(note);

        List<Note> notes = jdbcNotesDAO.getAll();
        assertNotNull(notes, "L'elenco delle note non dovrebbe essere nullo.");
        assertFalse(notes.isEmpty(), "Dovrebbe esserci almeno una nota salvata.");
    }

    @Test
    void testRetrieveNoteById() {
        Note note = new Note("12345", "testUser", "Nota Specifica", "Contenuto della nota");
        jdbcNotesDAO.save(note);

        Note retrieved = jdbcNotesDAO.getById("12345");
        assertNotNull(retrieved, "La nota dovrebbe essere recuperata correttamente.");
        assertEquals("12345", retrieved.getId(), "L'ID della nota deve corrispondere.");
        assertEquals("testUser", retrieved.getUid(), "L'UID della nota deve corrispondere.");
    }

    @Test
    void testDeleteNote() {
        Note note = new Note("67890", "testUser", "Nota da eliminare", "Questa nota sarà eliminata.");
        jdbcNotesDAO.save(note);

        jdbcNotesDAO.delete("67890");

        assertNull(jdbcNotesDAO.getById("67890"), "La nota eliminata non dovrebbe più esistere.");
    }
}
