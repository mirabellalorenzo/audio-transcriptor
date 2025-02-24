package control;

import entity.Note;
import entity.User;
import javafx.stage.Stage;

import config.AppConfig;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import view.gui1.LoginView;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {
    private final NotesDAO notesDAO = NotesDAOFactory.getNotesDAO();

    public String getUserEmail() {
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user.");
        }
        return currentUser.getEmail();
    }

    public String getUserPhotoUrl() {
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user.");
        }
        return currentUser.getPhotoUrl();
    }

    public void openPageView(Stage primaryStage, String pageName) {
        if (primaryStage == null || pageName == null || pageName.isBlank()) {
            throw new IllegalArgumentException("Invalid stage or page name.");
        }

        if ("Transcribe Audio".equals(pageName)) {
            if (AppConfig.getGuiMode() == AppConfig.GuiMode.GUI_1) {
                new view.gui1.TranscriptionView().start(primaryStage);
            } else {
                new view.gui2.TranscriptionView2().start(primaryStage);
            }
        } else if ("Notes".equals(pageName)) {
            if (AppConfig.getGuiMode() == AppConfig.GuiMode.GUI_1) {
                new view.gui1.HomeView().start(primaryStage);
            } else {
                new view.gui2.HomeView2().start(primaryStage);
            }
        } else {
            throw new IllegalArgumentException("Unrecognized page: " + pageName);
        }
    }

    private void openLoginView(Stage primaryStage) {
        if (primaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null.");
        }
        new LoginView().start(primaryStage);
    }

    public List<Note> getSavedNotes() {
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user.");
        }

        try {
            String currentUserId = currentUser.getId();
            List<Note> allNotes = notesDAO.getAll();

            return allNotes.stream()
                    .filter(note -> note.getUid().equals(currentUserId))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving notes: " + e.getMessage(), e);
        }
    }

    public Note createNewNote() {
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }

        Note newNote = new Note(null, currentUser.getId(), "New Note", "");
        try {
            notesDAO.save(newNote);
        } catch (IOException e) {
            throw new RuntimeException("Error saving new note: " + e.getMessage(), e);
        }
        return newNote;
    }

    public void updateNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("Cannot update a null note.");
        }
        try {
            notesDAO.save(note);
        } catch (IOException e) {
            throw new RuntimeException("Error updating note: " + e.getMessage(), e);
        }
    }

    public void deleteNote(Note note) {
        if (note == null || note.getId() == null) {
            throw new IllegalArgumentException("Cannot delete a null note or note with null ID.");
        }
        try {
            notesDAO.delete(note.getId());
        } catch (IOException e) {
            throw new RuntimeException("Error deleting note: " + e.getMessage(), e);
        }
    }

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }
}