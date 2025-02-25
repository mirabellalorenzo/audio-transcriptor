package view.gui1;

import boundary.HomeBoundary;
import control.NoteBean;
import config.AppConfig;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.components.SidebarComponent;
import view.components.NotesListComponent;
import view.components.NoteDetailComponent;
import java.util.ArrayList;
import java.util.List;

public class HomeView {
    private HomeBoundary boundary;
    private NoteDetailComponent noteDetail;
    private NotesListComponent notesList;
    private List<NoteBean> notes = new ArrayList<>();
    private Stage primaryStage;

    public HomeView(AppConfig appConfig) {
        this.boundary = new HomeBoundary(appConfig);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        AppConfig appConfig = new AppConfig();
        boundary = new HomeBoundary(appConfig);

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
                notes.isEmpty() ? new NoteBean(null, null, "No notes", "") : notes.get(0),
                new NoteDetailComponent.NoteChangeListener() {
                    @Override
                    public void onNoteUpdated(NoteBean noteBean) {
                        boundary.updateNote(noteBean);
                        notesList.refreshNotesList();
                    }

                    @Override
                    public void onNoteDeleted(NoteBean noteBean) {
                        notes.remove(noteBean);
                        boundary.deleteNote(noteBean);
                        notesList.refreshNotesList();
                    }
                },
                boundary
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

    private void updateSelectedNote(NoteBean noteBean) {
        noteDetail.updateNote(noteBean);
    }
}
