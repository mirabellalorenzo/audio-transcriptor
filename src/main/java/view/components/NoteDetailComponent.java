package view.components;

import boundary.HomeBoundary;
import entity.Note;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class NoteDetailComponent extends VBox {
    private final TextField titleField;
    private final TextArea contentArea;
    private final Button saveButton;
    private final Button deleteButton;
    private Note currentNote;
    private final NoteChangeListener listener;
    private final HomeBoundary boundary = new HomeBoundary();

    public interface NoteChangeListener {
        void onNoteUpdated(Note note);
        void onNoteDeleted(Note note);
    }

    public NoteDetailComponent(Note note, NoteChangeListener listener) {
        this.listener = listener;
        this.currentNote = note;
        this.setStyle(
            "-fx-padding: 40 85; " +  
            "-fx-background-color: white; " +
            "-fx-spacing: 15; "  
        );

        // **Immagine Profilo e Email in Alto a Destra**
        HBox topRightContainer = new HBox(10);
        topRightContainer.setAlignment(Pos.TOP_RIGHT);
        topRightContainer.setStyle("-fx-padding: 15px; -fx-spacing: 10px;");        

        ImageView profileImage = new ImageView(new Image(boundary.getUserPhotoUrl()));
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        profileImage.setPreserveRatio(true);
        profileImage.setStyle("-fx-background-radius: 50%; -fx-border-radius: 50%;");

        Label emailLabel = new Label(boundary.getUserEmail());
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        topRightContainer.getChildren().addAll(emailLabel, profileImage);
        this.getChildren().add(0, topRightContainer); // Aggiunge in alto


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
        contentArea.getStylesheets().add(getClass().getResource("/view/scrollbar.css").toExternalForm());
        contentArea.setWrapText(true);
        contentArea.setMaxWidth(700);
        contentArea.setMinWidth(400);
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

        saveButton = new CustomButtonComponent("Save Changes", CustomButtonComponent.ButtonType.PRIMARY);
        saveButton.setDisable(true);
        
        deleteButton = new CustomButtonComponent("Delete", CustomButtonComponent.ButtonType.OUTLINE);
        

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
