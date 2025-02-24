package boundary;

import control.HomeController;
import entity.Note;
import javafx.stage.Stage;
import java.util.List;

public class HomeBoundary {
    private final HomeController homeController = new HomeController();

    public String getUserEmail() {
        return homeController.getUserEmail();
    }

    public String getUserPhotoUrl() {
        return homeController.getUserPhotoUrl();
    }

    public List<Note> getSavedNotes() {
        return homeController.getSavedNotes();
    }

    public Note createNewNote() {
        return homeController.createNewNote();
    }

    public void updateNote(Note note) {
        if (note == null) {
            throw new IllegalArgumentException("No note provided for update.");
        }
        homeController.updateNote(note);
    }

    public void deleteNote(Note note) {
        homeController.deleteNote(note);
    }    

    public void logout(Stage primaryStage) {
        homeController.logout(primaryStage);
    }

    public void openPageView(Stage primaryStage, String pageName) {
        homeController.openPageView(primaryStage, pageName);
    }     
}
