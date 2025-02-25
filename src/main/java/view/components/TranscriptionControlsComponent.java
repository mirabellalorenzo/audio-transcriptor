package view.components;

import boundary.TranscriptionBoundary;
import control.TranscriptionBean;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class TranscriptionControlsComponent extends VBox {
    private final CustomButtonComponent uploadButton;

    private final TranscriptionBoundary boundary;
    private final ProgressBar progressBar;
    private final TranscriptionEditorComponent editorComponent;
    private final Consumer<TranscriptionBean> showSummaryPage;
    private final Label uploadInfoLabel;
    
    private final HBox uploadStateButtons;
    private final HBox initialStateButtons;
    private final HBox editingStateButtons;

    public TranscriptionControlsComponent(TranscriptionBoundary boundary, 
                                          TranscriptionEditorComponent editorComponent, 
                                          Consumer<TranscriptionBean> showSummaryPage,
                                          Stage primaryStage) {
        this.boundary = boundary;
        this.editorComponent = editorComponent;
        this.showSummaryPage = showSummaryPage;
        
        // Buttons
        uploadButton = new CustomButtonComponent("Select File", null, CustomButtonComponent.ButtonType.PRIMARY);
        CustomButtonComponent editButton = new CustomButtonComponent("Edit", null, CustomButtonComponent.ButtonType.SECONDARY);
        CustomButtonComponent saveButton = new CustomButtonComponent("Save", null, CustomButtonComponent.ButtonType.PRIMARY);
        CustomButtonComponent saveAndExitButton = new CustomButtonComponent("Save & Exit", null, CustomButtonComponent.ButtonType.PRIMARY);
        CustomButtonComponent cancelButton = new CustomButtonComponent("Cancel", null, CustomButtonComponent.ButtonType.OUTLINE);
        CustomButtonComponent restoreButton = new CustomButtonComponent("Reset", null, CustomButtonComponent.ButtonType.SECONDARY);

        // Events
        uploadButton.setOnAction(event -> handleUpload());
        editButton.setOnAction(event -> enableEditingMode());
        saveButton.setOnAction(event -> saveChanges());
        saveAndExitButton.setOnAction(event -> handleSaveAndExit());
        cancelButton.setOnAction(event -> cancelEdit());
        restoreButton.setOnAction(event -> restoreOriginal());

        // State 0: "Upload Audio"
        uploadInfoLabel = new Label("Accepted audio formats: MP3, WAV");
        uploadInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        uploadInfoLabel.setAlignment(Pos.CENTER);
        uploadInfoLabel.setVisible(true);

        uploadStateButtons = new HBox(10, uploadButton);
        uploadStateButtons.setAlignment(Pos.CENTER);
        uploadStateButtons.setManaged(true);
        uploadStateButtons.setVisible(true);

        // State 1: "Edit" + "Save & Exit"
        initialStateButtons = new HBox(10, editButton, saveAndExitButton);
        initialStateButtons.setAlignment(Pos.CENTER);
        initialStateButtons.setManaged(false);
        initialStateButtons.setVisible(false);

        // State 2: "Save", "Cancel" e "Reset"
        editingStateButtons = new HBox(10, saveButton, cancelButton, restoreButton);
        editingStateButtons.setAlignment(Pos.CENTER);
        editingStateButtons.setManaged(false);
        editingStateButtons.setVisible(false);

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);
        progressBar.setStyle(
            "-fx-accent: black;" +
            "-fx-background-radius: 10px;" +
            "-fx-border-radius: 10px;"
        );            
        progressBar.setVisible(false);        


        setSpacing(15);
        setStyle("-fx-alignment: center; -fx-pref-width: 100%;");

        getChildren().addAll(uploadInfoLabel, uploadStateButtons, initialStateButtons, editingStateButtons, progressBar);
    }

    private void handleUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select an audio file");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.ogg")
        );
    
        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
    
        if (selectedFile != null) {
            uploadInfoLabel.setVisible(false);
            uploadButton.setVisible(false);
    
            progressBar.setProgress(0);
            progressBar.setVisible(true);

            TranscriptionBean transcriptionBean = new TranscriptionBean();
            transcriptionBean.setFilePath(selectedFile.getAbsolutePath());
            transcriptionBean.setText("");

            boundary.uploadAudioAsync(transcriptionBean,
                    progress -> Platform.runLater(() -> progressBar.setProgress(progress)),
                    () -> Platform.runLater(() -> {
                        progressBar.setVisible(false);

                        TranscriptionBean transcription = transcriptionController.getTranscription();
                        editorComponent.loadTranscription(transcription);

                        uploadStateButtons.setManaged(false);
                        uploadStateButtons.setVisible(false);
                        initialStateButtons.setManaged(true);
                        initialStateButtons.setVisible(true);
                    }),
                    () -> Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        uploadButton.setVisible(true);
                        throw new IllegalStateException("Transcription process failed.");
                    })
            );
        }
    }    

    private void enableEditingMode() {
        editorComponent.enableEditing();

        initialStateButtons.setManaged(false);
        initialStateButtons.setVisible(false);

        editingStateButtons.setManaged(true);
        editingStateButtons.setVisible(true);
    }

    private void saveChanges() {
        editorComponent.saveChanges();

        TranscriptionBean transcription = boundary.getTranscription();
        transcription.setText(editorComponent.getCurrentText());
    
        boundary.updateTranscription(transcription);
    
        exitEditingMode();
    }    

    private void handleSaveAndExit() {
        saveChanges();

        TranscriptionBean transcription = boundary.getTranscription();

        if (transcription == null || transcription.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot save an empty transcription.");
        }
    
        showSummaryPage.accept(transcription);
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

        editingStateButtons.setManaged(false);
        editingStateButtons.setVisible(false);

        initialStateButtons.setManaged(true);
        initialStateButtons.setVisible(true);
    }
}
