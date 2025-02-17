package boundary;

import control.HomeController;
import entity.Note;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeBoundary {
    private HomeController homeController = new HomeController();
    private Note selectedNote;
    private static final Logger logger = LoggerFactory.getLogger(HomeBoundary.class);

    public String getUserEmail() {
        return homeController.getUserEmail();
    }

    public String getUserPhotoUrl() {
        return homeController.getUserPhotoUrl();
    }

    public List<Note> getSavedNotes() {
        return homeController.getSavedNotes();
    }

    public void loadNote(Note note, TextArea noteTextArea, Button saveNoteButton) {
        if (note != null) {
            this.selectedNote = note; 
            noteTextArea.setText(note.getContent()); 
            noteTextArea.setVisible(true);
            saveNoteButton.setVisible(true); 
            logger.info("Note loaded: {}", note.getTitle());
        } else {
            logger.warn("Attempted to load a null note.");
        }
    }    

    public Note createNewNote() {
        return homeController.createNewNote();
    }      

    public void updateNote(Note note) {
        if (note != null) {
            homeController.updateNote(note);
            selectedNote = note;
            logger.info("Note updated: {}", note.getTitle());
        } else {
            logger.warn("No note provided for update.");
        }
    }          

    public void deleteNote(Note note) {
        homeController.deleteNote(note);
    }    

    public void logout(Stage primaryStage) {
        logger.info("User logged out.");
        homeController.logout(primaryStage);
    }

    public void openPageView(Stage primaryStage, String pageName) {
        homeController.openPageView(primaryStage, pageName);
    }     
}
