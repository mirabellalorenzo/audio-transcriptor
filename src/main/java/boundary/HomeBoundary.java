package boundary;

import control.HomeController;
import control.TranscriptionController;
import entity.Note;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.util.List;

public class HomeBoundary {
    private HomeController homeController = new HomeController();
    private Note selectedNote;

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
        }
    }    

    public void updateNote() {
        if (selectedNote != null) {
            homeController.updateNote(selectedNote); // Ora usa selectedNote salvato
            System.out.println("✅ Nota aggiornata: " + selectedNote.getTitle());
        } else {
            System.err.println("❌ Nessuna nota selezionata.");
        }
    }        

    public void logout(Stage primaryStage) {
        homeController.logout(primaryStage);
    }

    public void openToolView(Stage primaryStage, String toolName) {
        System.out.println("openToolView chiamato con: " + toolName);
        if ("Transcribe Audio".equals(toolName)) {
            TranscriptionBoundary transcriptionBoundary = new TranscriptionBoundary(new TranscriptionController());
            transcriptionBoundary.openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " non gestito.");
        }
    }
}
