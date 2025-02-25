package view.components;

import boundary.TranscriptionBoundary;
import control.TranscriptionBean;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class TranscriptionEditorComponent extends VBox {
    private TextArea transcriptionTextArea;
    private TranscriptionBoundary boundary;
    private String originalText;
    private String lastSavedText;

    public TranscriptionEditorComponent(TranscriptionBoundary boundary) {
        this.boundary = boundary;

        Label titleLabel = new Label("Transcribe Audio");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #222;");

        this.transcriptionTextArea = new TextArea();
        transcriptionTextArea.setWrapText(true);
        transcriptionTextArea.setEditable(false);
        transcriptionTextArea.setPromptText("Il testo trascritto apparirà qui...");
        transcriptionTextArea.setPrefHeight(250);
        transcriptionTextArea.setPrefWidth(750);
        transcriptionTextArea.setMaxWidth(750);

        transcriptionTextArea.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 15px;" +  
            "-fx-border-radius: 12px; " +  
            "-fx-border-color: #E0E0E0; " +  
            "-fx-border-width: 1px; " +  
            "-fx-background-color: white; " +  
            "-fx-background-radius: 10px; " +  
            "-fx-focus-color: transparent; " +  
            "-fx-faint-focus-color: transparent; " +
            "-fx-control-inner-background: white;"
        );

        setSpacing(20);
        setStyle("-fx-alignment: center;");

        getChildren().addAll(titleLabel, transcriptionTextArea);
    }

    public void loadTranscription(TranscriptionBean transcription) {
        this.originalText = transcription.getText();
        this.lastSavedText = originalText;
        transcriptionTextArea.setText(originalText);
        transcriptionTextArea.setEditable(false);
    }

    public void enableEditing() {
        transcriptionTextArea.setEditable(true);
    }

    public void disableEditing() {
        transcriptionTextArea.setEditable(false);
    }

    public void saveChanges() {
        lastSavedText = transcriptionTextArea.getText();
    }

    public void cancelEdit() {
        transcriptionTextArea.setText(lastSavedText);
    }

    public void restoreOriginal() {
        transcriptionTextArea.setText(originalText);
    }

    public String getCurrentText() {
        return transcriptionTextArea.getText();
    }
}
