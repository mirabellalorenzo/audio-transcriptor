package view.components;

import boundary.TranscriptionBoundary;
import entity.Transcription;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;

import java.util.function.Consumer;

public class TranscriptionControlsComponent extends VBox {
    private CustomButtonComponent uploadButton, editButton, saveButton, cancelButton, restoreButton;
    private TranscriptionBoundary boundary;
    private TranscriptionEditorComponent editorComponent;
    private Consumer<Transcription> showSummaryPage;

    public TranscriptionControlsComponent(TranscriptionBoundary boundary, TranscriptionEditorComponent editorComponent, Consumer<Transcription> showSummaryPage) {
        this.boundary = boundary;
        this.editorComponent = editorComponent;
        this.showSummaryPage = showSummaryPage;
        

        uploadButton = new CustomButtonComponent("Carica Audio", null, CustomButtonComponent.ButtonType.PRIMARY);
        editButton = new CustomButtonComponent("Modifica", null, CustomButtonComponent.ButtonType.SECONDARY);
        saveButton = new CustomButtonComponent("Salva", null, CustomButtonComponent.ButtonType.PRIMARY);
        cancelButton = new CustomButtonComponent("Annulla", null, CustomButtonComponent.ButtonType.OUTLINE);
        restoreButton = new CustomButtonComponent("Ripristina", null, CustomButtonComponent.ButtonType.SECONDARY);

        editButton.setVisible(false);
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        restoreButton.setVisible(false);

        uploadButton.setOnAction(event -> handleUpload());
        editButton.setOnAction(event -> enableEditingMode());
        saveButton.setOnAction(event -> saveChanges());
        cancelButton.setOnAction(event -> cancelEdit());
        restoreButton.setOnAction(event -> restoreOriginal());

        HBox buttonContainer = new HBox(10, uploadButton, editButton, saveButton, cancelButton, restoreButton);
        buttonContainer.setStyle("-fx-alignment: center;");

        setSpacing(15);
        setStyle("-fx-alignment: center;");

        getChildren().add(buttonContainer);
    }

    private void handleUpload() {
        // Crea un FileChooser per selezionare file audio
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un file audio");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );
        
        // Usa getScene().getWindow() per ottenere la finestra attiva
        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        
        // Se l'utente ha selezionato un file, prosegui
        if (selectedFile != null) {
            boolean success = boundary.uploadAudio(selectedFile.getAbsolutePath());
            if (success) {
                Transcription transcription = boundary.getTranscription();
                editorComponent.loadTranscription(transcription);
                editButton.setVisible(true);
            }
        }
    }

    private void enableEditingMode() {
        editorComponent.enableEditing();
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        restoreButton.setVisible(true);
        editButton.setVisible(false);
    }

    private void saveChanges() {
        editorComponent.saveChanges();
        showSummaryPage.accept(boundary.getTranscription());
    }

    private void cancelEdit() {
        editorComponent.cancelEdit();
        exitEditingMode();
    }

    private void restoreOriginal() {
        editorComponent.restoreOriginal();
    }

    private void exitEditingMode() {
        editorComponent.disableEditing();
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
        restoreButton.setVisible(false);
        editButton.setVisible(true);
    }
}
