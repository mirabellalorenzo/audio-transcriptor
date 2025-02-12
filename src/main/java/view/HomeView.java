package view;

import boundary.HomeBoundary;
import entity.Note;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.components.SidebarComponent;
import view.components.NotesListComponent;
import view.components.NoteDetailComponent;

import java.util.ArrayList;
import java.util.List;

public class HomeView {
    private final HomeBoundary boundary = new HomeBoundary();
    private NoteDetailComponent noteDetail;
    private NotesListComponent notesList;
    private List<Note> notes = new ArrayList<>();

    public void start(Stage primaryStage) {
        notes = boundary.getSavedNotes(); 
        if (notes == null) {
            notes = new ArrayList<>();
        }
    
        // Istanzia il componente delle note prima della Sidebar
        notesList = new NotesListComponent(notes, this::updateSelectedNote);
    
        SidebarComponent sidebar = new SidebarComponent(
                boundary, 
                primaryStage, 
                boundary.getUserEmail(), 
                boundary.getUserPhotoUrl(), 
                notes, 
                notesList // ora questo riferimento è valido
        );
    
        noteDetail = new NoteDetailComponent(
                notes.isEmpty() ? new Note(null, null, "No notes", "") : notes.get(0), 
                new NoteDetailComponent.NoteChangeListener() {
                    @Override
                    public void onNoteUpdated(Note note) {
                        boundary.updateNote();
                        System.out.println("Nota aggiornata: " + note.getTitle());
                    }
    
                    @Override
                    public void onNoteDeleted(Note note) {
                        notes.remove(note);
                        boundary.deleteNote(note);
                        refreshNotesList();
                    }
                }
        );
    
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(notesList);
        root.setRight(noteDetail);
    
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    private void updateSelectedNote(Note note) {
        noteDetail.updateNote(note);
    }

    private void refreshNotesList() {
        notesList.getChildren().clear();
        notesList.getChildren().addAll(new NotesListComponent(notes, this::updateSelectedNote).getChildren());
    }
}
