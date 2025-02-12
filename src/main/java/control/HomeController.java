package control;

import entity.Note;
import entity.User;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import view.HomeView;
import view.LoginView;
import view.TranscriptionView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final NotesDAO notesDAO = NotesDAOFactory.getNotesDAO();

    public String getUserEmail() {
        User currentUser = AuthController.getCurrentUser();
        return currentUser != null ? currentUser.getEmail() : "Email not available";
    }

    public String getUserPhotoUrl() {
        User currentUser = AuthController.getCurrentUser();
        return currentUser != null ? currentUser.getPhotoUrl() : "/images/avatar.png";
    }

    public void openToolView(Stage primaryStage, String toolName) {
        logger.info("Tool selected: {}", toolName);
        if ("Transcribe Audio".equals(toolName)) {
            openTranscriptionView(primaryStage);
        }
    }

    private void openTranscriptionView(Stage primaryStage) {
        logger.info("Opening Transcription View.");
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public void openHome(Stage primaryStage) {
        logger.info("Opening Home View.");
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }
    
    private void openLoginView(Stage primaryStage) {
        logger.info("Opening Login View.");
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }

    public List<Note> getSavedNotes() {
        try {
            User currentUser = AuthController.getCurrentUser();
            if (currentUser == null) {
                logger.warn("Error: No authenticated user.");
                return List.of();
            }

            String currentUserId = currentUser.getId();
            List<Note> allNotes = notesDAO.getAll();
            
            List<Note> userNotes = allNotes.stream()
                    .filter(note -> note.getUid().equals(currentUserId))
                    .collect(Collectors.toList());

            logger.info("Retrieved {} notes for user: {}", userNotes.size(), currentUser.getEmail());
            return userNotes;
        } catch (Exception e) {
            logger.error("Error retrieving notes: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public Note createNewNote() {
        try {
            User currentUser = AuthController.getCurrentUser();
            if (currentUser == null) {
                logger.warn("Attempted to create a note but no user is logged in.");
                return null;
            }
    
            Note newNote = new Note(null, currentUser.getId(), "New Note", "");
            notesDAO.save(newNote);
            logger.info("New note created: {}", newNote.getTitle());
            return newNote;
        } catch (Exception e) {
            logger.error("Error creating new note: {}", e.getMessage(), e);
            return null;
        }
    }    
    
    public void updateNote(Note note) {
        try {
            notesDAO.save(note);
            logger.info("Note successfully updated in Firebase: {}", note.getTitle());
        } catch (Exception e) {
            logger.error("Error updating note: {}", e.getMessage(), e);
        }
    }    

    public void deleteNote(Note note) {
        try {
            notesDAO.delete(note.getId());
            logger.info("Nota eliminata con successo: {}", note.getTitle());
        } catch (IOException e) {
            logger.error("Errore durante l'eliminazione della nota: {}", e.getMessage(), e);
        }
    } 

    public void logout(Stage primaryStage) {
        logger.info("User logged out.");
        AuthController.logout();
        openLoginView(primaryStage);
    }  
}
