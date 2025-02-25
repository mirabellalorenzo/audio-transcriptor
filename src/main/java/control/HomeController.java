package control;

import entity.Note;
import javafx.stage.Stage;

import config.AppConfig;
import persistence.NotesDAO;
import persistence.NotesDAOFactory;
import view.gui1.LoginView;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {
    private final NotesDAO notesDAO;
    private final AppConfig appConfig;

    public HomeController(AppConfig appConfig) {
        if (appConfig == null) {
            throw new IllegalArgumentException("AppConfig cannot be null");
        }
        this.appConfig = appConfig;
        this.notesDAO = NotesDAOFactory.getNotesDAO(appConfig);
    }

    public String getUserEmail() {
        UserBean currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user.");
        }
        return currentUser.getEmail();
    }

    public String getUserPhotoUrl() {
        UserBean currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user. ");
        }
        return currentUser.getPhotoUrl();
    }

    public void openPageView(Stage primaryStage, String pageName) {
        if (primaryStage == null || pageName == null || pageName.isBlank()) {
            throw new IllegalArgumentException("Invalid stage or page name.");
        }

        if ("Transcribe Audio".equals(pageName)) {
            if (appConfig.getGuiMode() == AppConfig.GuiMode.GUI_1) {
                new view.gui1.TranscriptionView(appConfig).start(primaryStage);
            } else {
                new view.gui2.TranscriptionView2(appConfig).start(primaryStage);
            }
        } else if ("Notes".equals(pageName)) {
            if (appConfig.getGuiMode() == AppConfig.GuiMode.GUI_1) {
                new view.gui1.HomeView(appConfig).start(primaryStage);
            } else {
                new view.gui2.HomeView2(appConfig).start(primaryStage);
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

    public List<NoteBean> getSavedNotes() {
        UserBean currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No authenticated user");
        }

        try {
            String currentUserId = currentUser.getId();
            List<Note> allNotes = notesDAO.getAll();

            return allNotes.stream()
                    .filter(note -> note.getUid().equals(currentUserId))
                    .map(note -> new NoteBean(note.getId(), note.getUid(), note.getTitle(), note.getContent()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException("Error retrieving notes", e);
        }
    }

    public NoteBean createNewNote() {
        UserBean currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            throw new IllegalStateException("No user is logged in.");
        }

        Note newNote = new Note(null, currentUser.getId(), "New Note", "");
        try {
            notesDAO.save(newNote);
            return new NoteBean(newNote.getId(), newNote.getUid(), newNote.getTitle(), newNote.getContent());
        } catch (IOException e) {
            throw new IllegalStateException("Error saving new note", e);
        }
    }

    public void updateNote(NoteBean noteBean) {
        if (noteBean == null) {
            throw new IllegalArgumentException("Cannot update a null note.");
        }
        try {
            Note note = new Note(noteBean.getId(), noteBean.getUserId(), noteBean.getTitle(), noteBean.getContent());
            notesDAO.save(note);
        } catch (IOException e) {
            throw new IllegalStateException("Error updating note", e);
        }
    }

    public void deleteNote(NoteBean noteBean) {
        if (noteBean == null || noteBean.getId() == null) {
            throw new IllegalArgumentException("Cannot delete a null note or note with null ID.");
        }
        try {
            notesDAO.delete(noteBean.getId());
        } catch (IOException e) {
            throw new IllegalStateException("Error deleting note", e);
        }
    }

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }
}