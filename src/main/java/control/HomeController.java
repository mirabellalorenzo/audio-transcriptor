package control;

import entity.Note;
import javafx.stage.Stage;
import persistence.FirebaseNotesDAO;
import view.HomeView;
import view.LoginView;
import view.TranscriptionView;

import java.util.List;

public class HomeController {
    private static final FirebaseNotesDAO notesDAO = new FirebaseNotesDAO(); // Iniezione del DAO

    public static void openToolView(Stage primaryStage, String toolName) {
        if ("Transcribe Audio".equals(toolName)) {
            openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " selected");
        }
    }

    private static void openTranscriptionView(Stage primaryStage) {
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public static void displaySavedNotes() {
        try {
            List<Note> notes = notesDAO.getAll(); // Recupera tutte le note
            System.out.println("Note salvate:");
            for (Note note : notes) {
                System.out.println("ID: " + note.getId() + ", Testo: " + note.getContent());
            }
        } catch (Exception e) {
            System.err.println("Errore durante il recupero delle note: " + e.getMessage());
        }
    }

    public static void logout(Stage primaryStage) {
        AuthController.logout();
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }

    public void openHome(Stage primaryStage) {
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }
}
