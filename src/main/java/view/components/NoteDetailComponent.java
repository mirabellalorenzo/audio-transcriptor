package view.components;

import entity.Note;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NoteDetailComponent extends VBox {
    private final TextField titleField; // 🔥 Titolo modificabile
    private final TextArea contentArea;
    private final Button saveButton;
    private final Button deleteButton;
    private Note currentNote;
    private final NoteChangeListener listener;

    public interface NoteChangeListener {
        void onNoteUpdated(Note note);
        void onNoteDeleted(Note note);
    }

    public NoteDetailComponent(Note note, NoteChangeListener listener) {
        this.listener = listener;
        this.currentNote = note;
        this.setStyle(
            "-fx-padding: 100; " +  
            "-fx-background-color: white; " +
            "-fx-spacing: 15; "  
        );

        titleField = new TextField(note.getTitle() == null || note.getTitle().isBlank() ? "New Note" : note.getTitle());
        titleField.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-border-radius: 12px; " + 
            "-fx-border-color: #E0E0E0; " +
            "-fx-border-width: 1px; " +
            "-fx-border-insets: 0; " +
            "-fx-padding: 8px 12px; " +
            "-fx-background-radius: 12px; " +
            "-fx-background-insets: 0; " +
            "-fx-background-color: white; " +
            "-fx-focus-color: transparent; " +
            "-fx-faint-focus-color: transparent; " +
            "-fx-effect: none;"
        );

        titleField.setPrefWidth(400); 

        contentArea = new TextArea(note.getContent());
        contentArea.setWrapText(true);
        contentArea.setPrefWidth(700);
        contentArea.setPrefHeight(250);
        contentArea.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 15px; " +  
            "-fx-border-radius: 12px; " +  
            "-fx-border-color: #E0E0E0; " +  
            "-fx-border-width: 1px; " +  
            "-fx-border-insets: 0; " +
            "-fx-background-color: white; " +  
            "-fx-background-insets: 0; " +
            "-fx-background-radius: 12px; " +  
            "-fx-focus-color: transparent; " +  
            "-fx-faint-focus-color: transparent; " +
            "-fx-background: white; " +
            "-fx-background-insets: 0; " +
            "-fx-background-padding: 0; " +
            "-fx-border-insets: 0; " +
            "-fx-box-border: transparent; " +
            "-fx-effect: none; " +
            "-fx-text-box-border: transparent; " +
            "-fx-control-inner-background: white;"
        );        

        saveButton = new Button("Save Changes");
        saveButton.setDisable(true);
        saveButton.setStyle(
            "-fx-background-color: #0078d7; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10; " +
            "-fx-border-radius: 12px; " +
            "-fx-cursor: hand;"
        );

        deleteButton = new Button("Delete");
        deleteButton.setStyle(
            "-fx-background-color: #d9534f; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10; " +
            "-fx-border-radius: 12px; " +
            "-fx-cursor: hand;"
        );

        titleField.textProperty().addListener((obs, oldText, newText) -> {
            saveButton.setDisable(newText.equals(currentNote.getTitle()) && contentArea.getText().equals(currentNote.getContent()));
        });

        contentArea.textProperty().addListener((obs, oldText, newText) -> {
            saveButton.setDisable(newText.equals(currentNote.getContent()) && titleField.getText().equals(currentNote.getTitle()));
        });

        saveButton.setOnAction(e -> {
            currentNote.setTitle(titleField.getText());
            currentNote.setContent(contentArea.getText());
            listener.onNoteUpdated(currentNote);
        });

        deleteButton.setOnAction(e -> listener.onNoteDeleted(currentNote));

        HBox buttonsBox = new HBox(10, deleteButton, saveButton);
        this.getChildren().addAll(titleField, contentArea, buttonsBox);
    }

    public void updateNote(Note note) {
        this.currentNote = note;
        titleField.setText(note.getTitle() == null || note.getTitle().isBlank() ? "New Note" : note.getTitle());
        contentArea.setText(note.getContent());
        saveButton.setDisable(true);
    }
}
