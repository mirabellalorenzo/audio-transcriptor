package view.gui1;

import boundary.HomeBoundary;
import entity.Note;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.components.SidebarComponent;
import view.gui2.HomeView2;
import view.components.NotesListComponent;
import view.components.NoteDetailComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class HomeView {
    private static final Logger logger = LoggerFactory.getLogger(HomeView2.class);
    private final HomeBoundary boundary = new HomeBoundary();
    private NoteDetailComponent noteDetail;
    private NotesListComponent notesList;
    private List<Note> notes = new ArrayList<>();
    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        notes = boundary.getSavedNotes(); 
        if (notes == null) {
            notes = new ArrayList<>();
        }
    
        notesList = new NotesListComponent(boundary, primaryStage, notes, this::updateSelectedNote);
    
        SidebarComponent sidebar = new SidebarComponent(
                boundary, 
                primaryStage, 
                boundary.getUserEmail(), 
                boundary.getUserPhotoUrl(), 
                notes, 
                notesList
        );
    
        noteDetail = new NoteDetailComponent(
            notes.isEmpty() ? new Note(null, null, "No notes", "") : notes.get(0),
            new NoteDetailComponent.NoteChangeListener() {
                @Override
                public void onNoteUpdated(Note note) {
                    boundary.updateNote(note);
                    notesList.refreshNotesList();
                    logger.info("Nota aggiornata: {}", note.getTitle());
                }

                @Override
                public void onNoteDeleted(Note note) {
                    notes.remove(note);
                    boundary.deleteNote(note);
                    notesList.refreshNotesList();
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
}
