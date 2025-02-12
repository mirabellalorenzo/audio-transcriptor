package view.components;

import entity.Note;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NoteDetailComponent extends VBox {
    private final Label titleLabel;
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
            "-fx-padding: 100; " +  // Più spazio intorno
            "-fx-background-color: white; " +
            "-fx-spacing: 15; "  // Maggior spazio tra gli elementi
        );
        

        // Salviamo il titolo come variabile di istanza
        titleLabel = new Label(note.getTitle() == null || note.getTitle().isBlank() ? "New Note" : note.getTitle());
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        contentArea = new TextArea(note.getContent());
        contentArea.setWrapText(true);
        contentArea.setPrefWidth(700);  // Aumenta la larghezza
        contentArea.setPrefHeight(250); // Aumenta l'altezza
        contentArea.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 15px; " +  // Più spazio interno
            "-fx-border-radius: 12px; " +  // Bordo stondato
            "-fx-border-color: #E0E0E0; " +  // Bordo sottile grigio chiaro
            "-fx-border-width: 1px; " +  // Spessore del bordo sottile
            "-fx-background-color: white; " +  // Sfondo bianco uniforme
            "-fx-background-insets: 0, 0, 0; " +  // 🔥 Rimuove il bordo interno!
            "-fx-background-radius: 12px; " +  // 🔥 Rende il bordo uniforme
            "-fx-focus-color: transparent; " +  // 🔥 Rimuove il bordo blu quando è attivo!
            "-fx-faint-focus-color: transparent;"  // 🔥 Rimuove il bordo sfocato quando è selezionato!
        );
          


        // Disabilita il pulsante di salvataggio finché non ci sono modifiche
        saveButton = new Button("Salva Modifiche");
        saveButton.setDisable(true);
        saveButton.setStyle(
            "-fx-background-color: #0078d7; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10; " +
            "-fx-border-radius: 12px; " +
            "-fx-cursor: hand;"
        );

        deleteButton = new Button("Elimina");
        deleteButton.setStyle(
            "-fx-background-color: #d9534f; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10; " +
            "-fx-border-radius: 12px; " +
            "-fx-cursor: hand;"
        );

        // Attiva il pulsante Salva quando il testo cambia
        contentArea.textProperty().addListener((obs, oldText, newText) -> {
            saveButton.setDisable(newText.equals(currentNote.getContent()));
        });

        saveButton.setOnAction(e -> {
            currentNote.setContent(contentArea.getText());
            listener.onNoteUpdated(currentNote);
        });

        deleteButton.setOnAction(e -> listener.onNoteDeleted(currentNote));

        HBox buttonsBox = new HBox(10, deleteButton, saveButton);
        this.getChildren().addAll(titleLabel, contentArea, buttonsBox);
    }

    // Metodo per aggiornare la nota visualizzata
    public void updateNote(Note note) {
        this.currentNote = note;
        titleLabel.setText(note.getTitle() == null || note.getTitle().isBlank() ? "New Note" : note.getTitle()); // ✅ Aggiorniamo anche il titolo!
        contentArea.setText(note.getContent());
        saveButton.setDisable(true);
    }
}
