package control;

import entity.Note;
import entity.User;
import javafx.stage.Stage;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import view.HomeView;
import view.LoginView;
import view.TranscriptionView;

import java.util.List;
import java.util.stream.Collectors;

public class HomeController {
    private final NotesDAO notesDAO = NotesDAOFactory.getNotesDAO();

    public String getUserEmail() {
        User currentUser = AuthController.getCurrentUser();
        return currentUser != null ? currentUser.getEmail() : "Email non disponibile";
    }

    public String getUserPhotoUrl() {
        User currentUser = AuthController.getCurrentUser();
        return currentUser != null ? currentUser.getPhotoUrl() : "/images/avatar.png";
    }

    public void openToolView(Stage primaryStage, String toolName) {
        if ("Transcribe Audio".equals(toolName)) {
            openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " selected");
        }
    }

    private void openTranscriptionView(Stage primaryStage) {
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public void openHome(Stage primaryStage) {
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }
    
    private void openLoginView(Stage primaryStage) {
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }

    public List<Note> getSavedNotes() {
        try {
            User currentUser = AuthController.getCurrentUser();
            if (currentUser == null) {
                System.err.println("Errore: nessun utente autenticato.");
                return List.of();
            }

            String currentUserId = currentUser.getId();
            List<Note> allNotes = notesDAO.getAll();
            
            List<Note> userNotes = allNotes.stream()
                    .filter(note -> note.getUid().equals(currentUserId))
                    .collect(Collectors.toList());

            return userNotes;
        } catch (Exception e) {
            System.err.println("Errore durante il recupero delle note: " + e.getMessage());
            return List.of();
        }
    }

    public void updateNote(Note note) {
        try {
            notesDAO.save(note);
            System.out.println("✅ Nota aggiornata nel database Firebase!");
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'aggiornamento della nota.");
        }
    }    

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }  
}
