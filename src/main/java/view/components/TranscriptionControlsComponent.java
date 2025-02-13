package view.components;

import boundary.TranscriptionBoundary;
import entity.Transcription;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.function.Consumer;

public class TranscriptionControlsComponent extends VBox {
    private CustomButtonComponent uploadButton, editButton, saveButton, saveAndExitButton, cancelButton, restoreButton;
    private TranscriptionBoundary boundary;
    private TranscriptionEditorComponent editorComponent;
    private Consumer<Transcription> showSummaryPage;
    private Stage primaryStage;
    
    private HBox uploadStateButtons, initialStateButtons, editingStateButtons;

    public TranscriptionControlsComponent(TranscriptionBoundary boundary, 
                                          TranscriptionEditorComponent editorComponent, 
                                          Consumer<Transcription> showSummaryPage,
                                          Stage primaryStage) {
        this.boundary = boundary;
        this.editorComponent = editorComponent;
        this.showSummaryPage = showSummaryPage;
        this.primaryStage = primaryStage;
        
        // Dichiarazione dei pulsanti
        uploadButton = new CustomButtonComponent("Upload Audio", null, CustomButtonComponent.ButtonType.PRIMARY);
        editButton = new CustomButtonComponent("Edit", null, CustomButtonComponent.ButtonType.SECONDARY);
        saveButton = new CustomButtonComponent("Save", null, CustomButtonComponent.ButtonType.PRIMARY);
        saveAndExitButton = new CustomButtonComponent("Save & Exit", null, CustomButtonComponent.ButtonType.PRIMARY);
        cancelButton = new CustomButtonComponent("Cancel", null, CustomButtonComponent.ButtonType.OUTLINE);
        restoreButton = new CustomButtonComponent("Reset", null, CustomButtonComponent.ButtonType.SECONDARY);

        // Eventi dei pulsanti
        uploadButton.setOnAction(event -> handleUpload());
        editButton.setOnAction(event -> enableEditingMode());
        saveButton.setOnAction(event -> saveChanges());
        saveAndExitButton.setOnAction(event -> handleSaveAndExit());
        cancelButton.setOnAction(event -> cancelEdit());
        restoreButton.setOnAction(event -> restoreOriginal());

        // Stato 0: Solo "Carica Audio"
        uploadStateButtons = new HBox(10, uploadButton);
        uploadStateButtons.setStyle("-fx-alignment: center; -fx-pref-width: 100%;");
        uploadStateButtons.setManaged(true);
        uploadStateButtons.setVisible(true);

        // Stato 1: "Modifica" + "Salva ed Esci"
        initialStateButtons = new HBox(10, editButton, saveAndExitButton);
        initialStateButtons.setStyle("-fx-alignment: center; -fx-pref-width: 100%;");
        initialStateButtons.setManaged(false);
        initialStateButtons.setVisible(false);

        // Stato 2: "Salva", "Annulla" e "Ripristina"
        editingStateButtons = new HBox(10, saveButton, cancelButton, restoreButton);
        editingStateButtons.setStyle("-fx-alignment: center; -fx-pref-width: 100%;");
        editingStateButtons.setManaged(false);
        editingStateButtons.setVisible(false);

        setSpacing(15);
        setStyle("-fx-alignment: center; -fx-pref-width: 100%;");

        getChildren().addAll(uploadStateButtons, initialStateButtons, editingStateButtons);
    }

    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un file audio");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );

        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());

        if (selectedFile != null) {
            boolean success = boundary.uploadAudio(selectedFile.getAbsolutePath());
            if (success) {
                Transcription transcription = boundary.getTranscription();
                editorComponent.loadTranscription(transcription);

                // Cambia dallo Stato 0 (upload) allo Stato 1 (modifica e salva ed esci)
                uploadStateButtons.setManaged(false);
                uploadStateButtons.setVisible(false);

                initialStateButtons.setManaged(true);
                initialStateButtons.setVisible(true);
            }
        }
    }

    private void enableEditingMode() {
        editorComponent.enableEditing();

        // Cambia dallo Stato 1 (modifica e salva ed esci) allo Stato 2 (salva, annulla, ripristina)
        initialStateButtons.setManaged(false);
        initialStateButtons.setVisible(false);

        editingStateButtons.setManaged(true);
        editingStateButtons.setVisible(true);
    }

    private void saveChanges() {
        editorComponent.saveChanges();
    
        Transcription transcription = boundary.getTranscription();
        transcription.setText(editorComponent.getCurrentText());
    
        boundary.updateTranscription(transcription);
    
        exitEditingMode();
    }    

    private void handleSaveAndExit() {
        saveChanges();
    
        boolean saved = boundary.saveTranscription(primaryStage);
        if (saved) {
            exitEditingMode();
            showSummaryPage.accept(boundary.getTranscription());
        }
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

        // Torna allo Stato 1 (Modifica e Salva ed Esci)
        editingStateButtons.setManaged(false);
        editingStateButtons.setVisible(false);

        initialStateButtons.setManaged(true);
        initialStateButtons.setVisible(true);
    }
}
