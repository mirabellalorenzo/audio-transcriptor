package view.components;

import entity.Note;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class NotesListComponent extends VBox {
    public interface NoteSelectionListener {
        void onNoteSelected(Note note);
    }

    private final VBox notesContainer;
    private VBox selectedNoteCard = null;
    private final NoteSelectionListener listener;
    private final List<Note> notes;

    public NotesListComponent(List<Note> notes, NoteSelectionListener listener) {
        this.notes = notes;
        this.listener = listener;
        this.setStyle("-fx-padding: 20; -fx-spacing: 15; -fx-background-color: white; -fx-border-radius: 15px;");
        
        Label titleLabel = new Label("Notes");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #222;");

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

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.web("#888"));

        HBox.setHgrow(searchField, Priority.ALWAYS);  

        HBox searchBox = new HBox(10, searchIcon, searchField);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.setStyle(
            "-fx-background-color: #FFFFFF; " +
            "-fx-border-radius: 30px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-padding: 8px 16px; " +
            "-fx-pref-width: 100%; "
        );

        notesContainer = new VBox(10);
        notesContainer.setStyle("-fx-spacing: 10; -fx-padding: 5;");
        loadNotes(notes, listener);

        this.getChildren().addAll(titleLabel, searchBox, notesContainer);

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
            VBox noteCard = createNoteCard(note);

            noteCard.setOnMouseEntered(e -> {
                if (selectedNoteCard != noteCard) {
                    noteCard.setStyle(
                        "-fx-background-color: #EEEEEE; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #DADADA; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });

            noteCard.setOnMouseExited(e -> {
                if (selectedNoteCard != noteCard) {
                    noteCard.setStyle(
                        "-fx-background-color: #fcfbfc; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }
            });

            noteCard.setOnMouseClicked(e -> {
                if (selectedNoteCard != null) {
                    selectedNoteCard.setStyle(
                        "-fx-background-color: #fcfbfc; " +
                        "-fx-padding: 20px; " +
                        "-fx-border-radius: 20px; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-background-insets: 0; " + 
                        "-fx-background-radius: 20px; " +
                        "-fx-spacing: 10px; " +
                        "-fx-cursor: hand;"
                    );
                }

                selectedNoteCard = noteCard;

                selectedNoteCard.setStyle(
                    "-fx-background-color: #edf6ff; " +
                    "-fx-padding: 20px; " +
                    "-fx-border-radius: 20px; " +
                    "-fx-border-color: #99c9ef; " +
                    "-fx-background-insets: 0; " + 
                    "-fx-background-radius: 20px; " +
                    "-fx-spacing: 10px; " +
                    "-fx-cursor: hand;"
                );

                listener.onNoteSelected(note);
            });

            notesContainer.getChildren().add(noteCard);
        }
    }    

    public void addNoteAndSelect(Note newNote) {
        notes.add(newNote);
    
        VBox noteCard = createNoteCard(newNote);
        notesContainer.getChildren().add(0, noteCard);
        listener.onNoteSelected(newNote);
    }
    
    private VBox createNoteCard(Note note) {
        VBox noteCard = new VBox();
        noteCard.setStyle(
            "-fx-background-color: #fcfbfc; " +
            "-fx-padding: 20px; " +
            "-fx-border-radius: 20px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-background-insets: 0; " +
            "-fx-background-radius: 20px; " +
            "-fx-spacing: 10px; " +
            "-fx-cursor: hand;"
        );

        Label title = new Label(note.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #333;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setWrapText(true);

        Label preview = new Label(note.getContent());
        preview.setWrapText(true);
        preview.setMaxWidth(Double.MAX_VALUE);
        preview.setPrefHeight(34);
        preview.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        noteCard.getChildren().addAll(title, preview);
        return noteCard;
    }
}
