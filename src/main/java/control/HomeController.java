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
            return notesDAO.getAll(); // Recupera tutte le note
        } catch (Exception e) {
            System.err.println("Errore durante il recupero delle note: " + e.getMessage());
            return List.of(); // Ritorna una lista vuota in caso di errore
        }
    }

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }  
}
