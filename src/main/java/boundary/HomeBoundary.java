package boundary;

import control.HomeController;
import control.TranscriptionController;
import entity.Note;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import view.HomeView;
import view.components.NotesListComponent;

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
        Note newNote = homeController.createNewNote(); 
        return newNote;
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

    public void openToolView(Stage primaryStage, String toolName) {
        logger.info("openToolView called with: {}", toolName);
        
        if ("Transcribe Audio".equals(toolName)) {
            TranscriptionBoundary transcriptionBoundary = new TranscriptionBoundary(new TranscriptionController());
            transcriptionBoundary.openTranscriptionView(primaryStage);
            logger.info("Transcription tool opened.");
        } else if ("Notes".equals(toolName)) { // Gestione del tool Notes
            HomeView homeView = new HomeView();
            homeView.start(primaryStage);
            logger.info("HomeView (Notes) opened.");
        } else {
            logger.warn("Unrecognized tool: {}", toolName);
        }
    }
}
