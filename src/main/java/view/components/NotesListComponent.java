package view.components;

import entity.Note;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class NotesListComponent extends VBox {
    public interface NoteSelectionListener {
        void onNoteSelected(Note note);
    }

    private final VBox notesContainer;

    public NotesListComponent(List<Note> notes, NoteSelectionListener listener) {
        this.setStyle("-fx-padding: 20; -fx-spacing: 15; -fx-background-color: white; -fx-border-radius: 15px;");
        
        // **Titolo "Notes"**
        Label titleLabel = new Label("Notes");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #222;");

        // **Barra di ricerca**
        TextField searchField = new TextField();
        searchField.setPromptText("Search notes...");
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: transparent; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8px 12px; " +
            "-fx-border-radius: 30px; " +
            "-fx-pref-width: 100%; " +
            "-fx-text-fill: black;" +
            "-fx-prompt-text-fill: #999;"
        );

        // 🔥 Fai in modo che il TextField cresca dentro HBox
        HBox.setHgrow(searchField, Priority.ALWAYS);  

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.web("#888"));

        HBox searchBox = new HBox(10, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-border-radius: 30px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-padding: 8px 16px; " +
            "-fx-pref-width: 100%; "
        );        

        // **Container per le note**
        notesContainer = new VBox(10);
        notesContainer.setStyle("-fx-spacing: 10; -fx-padding: 5;");
        loadNotes(notes, listener);

        // **Aggiungiamo gli elementi al layout**
        this.getChildren().addAll(titleLabel, searchBox, notesContainer);

        // **Filtraggio note con la barra di ricerca**
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            List<Note> filteredNotes = notes.stream()
                .filter(note -> note.getTitle().toLowerCase().contains(newText.toLowerCase()))
                .collect(Collectors.toList());
            notesContainer.getChildren().clear();
            loadNotes(filteredNotes, listener);
        });
    }

    private void loadNotes(List<Note> notes, NoteSelectionListener listener) {
        for (Note note : notes) {
            VBox noteCard = new VBox();
            noteCard.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-padding: 12; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-color: #E0E0E0; " +
                "-fx-spacing: 6; " +
                "-fx-cursor: hand;"
            );

            // **Icona della nota**
            FontIcon noteIcon = new FontIcon(FontAwesomeSolid.STICKY_NOTE);
            noteIcon.setIconSize(16);
            noteIcon.setIconColor(Color.web("#0078D7")); // Blu

            Label title = new Label(note.getTitle());
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #333;");

            Label preview = new Label(note.getContent().length() > 80 ? note.getContent().substring(0, 80) + "..." : note.getContent());
            preview.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");


            HBox titleRow = new HBox(10, noteIcon, title);
            noteCard.getChildren().addAll(titleRow, preview);

            
            noteCard.setOnMouseEntered(e -> noteCard.setStyle(
                "-fx-background-color: #EEE; " + 
                "-fx-padding: 12; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-color: #DADADA; " +
                "-fx-spacing: 6; " +
                "-fx-cursor: hand;"
            ));

            noteCard.setOnMouseExited(e -> noteCard.setStyle(
                "-fx-background-color: #ffffff; " +
                "-fx-padding: 12; " +
                "-fx-border-radius: 20px; " +
                "-fx-border-color: #E0E0E0; " +
                "-fx-spacing: 6; " +
                "-fx-cursor: hand;"
            ));

            // **Selezione della nota**
            noteCard.setOnMouseClicked(e -> listener.onNoteSelected(note));

            notesContainer.getChildren().add(noteCard);
        }
    }
}
