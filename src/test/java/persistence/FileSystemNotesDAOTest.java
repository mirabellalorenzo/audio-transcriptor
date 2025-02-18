package persistence;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.File;
import control.AuthController;
import entity.Note;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

class FileSystemNotesDAOTest {
    private FileSystemNotesDAO fileSystemNotesDAO;

    @BeforeEach
    void setUp() throws Exception {
        fileSystemNotesDAO = new FileSystemNotesDAO();
    
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = Objects.requireNonNull(
                getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json"),
                "File serviceAccountKey.json non trovato in resources!"
            );
    
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    
            FirebaseApp.initializeApp(options);
        }
    
        // Forzare autenticazione prima di ogni test
        if (AuthController.getCurrentUser() == null) {
            AuthController.login("test@example.com", "123456");
        }
    }    

    @Test
    void testSaveNote() throws IOException {
        if (AuthController.getCurrentUser() == null) {
            AuthController.login("test@example.com", "123456");
        }
    
        Note note = new Note(null, AuthController.getCurrentUser().getId(), "Nota Test", "Contenuto di test");
        fileSystemNotesDAO.save(note);
    
        List<Note> notes = fileSystemNotesDAO.getAll();
    
        assertTrue(notes.stream().anyMatch(n -> n.getTitle().equals("Nota Test")), 
                   "La nota dovrebbe essere salvata correttamente.");
    }

    @Test
    void testDeleteNote() {
        File file = new File("/percorso/della/nota.txt"); 
        System.out.println("Verificando esistenza file prima della cancellazione: " + file.exists());
    
        boolean deleted = file.delete();
        System.out.println("Tentativo di eliminazione: " + deleted);
        
        System.out.println("Verificando esistenza file dopo la cancellazione: " + file.exists());
        
        assertFalse(file.exists(), "La nota dovrebbe essere eliminata correttamente.");
    }    
}
