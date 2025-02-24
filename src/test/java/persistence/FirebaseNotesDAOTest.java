//package persistence;
//
//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import entity.Note;
//import java.util.List;
//
//class FirebaseNotesDAOTest {
//    private FirebaseNotesDAO firebaseNotesDAO;
//
//    @BeforeEach
//    void setUp() {
//        firebaseNotesDAO = new FirebaseNotesDAO();
//    }
//
//    @Test
//    void testSaveNote() {
//        Note note = new Note(null, "testUser", "Nota di test", "Questo è il contenuto della nota");
//        assertDoesNotThrow(() -> firebaseNotesDAO.save(note), "Il salvataggio su Firebase non dovrebbe generare errori.");
//    }
//
//    @Test
//    void testRetrieveNotes() {
//        List<Note> notes = firebaseNotesDAO.getAll();
//        assertNotNull(notes, "L'elenco delle note non dovrebbe essere nullo.");
//    }
//}
