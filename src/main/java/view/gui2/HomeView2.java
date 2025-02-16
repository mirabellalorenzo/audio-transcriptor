package view.gui2;

import boundary.HomeBoundary;
import entity.Note;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.components.FlatNotesListComponent;
import view.components.NavbarComponent;

import java.util.List;

public class HomeView2 {
    private final HomeBoundary boundary = new HomeBoundary();
    private Stage primaryStage;
    
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        List<Note> notes = boundary.getSavedNotes();

        // Recupera i dati dell'utente
        String userEmail = boundary.getUserEmail();
        String userPhotoUrl = boundary.getUserPhotoUrl();

        NavbarComponent navbar = new NavbarComponent(boundary, primaryStage, userEmail, userPhotoUrl);
        FlatNotesListComponent flatNotesListComponent = new FlatNotesListComponent(boundary, primaryStage, notes, this::openNoteDetail);

        BorderPane content = new BorderPane();
        content.setTop(navbar);
        content.setCenter(flatNotesListComponent);

        StackPane root = new StackPane();
        root.getChildren().add(content);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Notes - Home");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void openNoteDetail(Note note) {
        System.out.println("Nota selezionata: " + note.getTitle());
    }
}
