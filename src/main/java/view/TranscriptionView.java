package view;

import boundary.TranscriptionBoundary;
import control.TranscriptionController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;

public class TranscriptionView extends Application {
    private String lastSavedText = "";
    private String originalText = "";

    @Override
    public void start(Stage primaryStage) {
        TranscriptionController controller = new TranscriptionController();
        TranscriptionBoundary boundary = new TranscriptionBoundary(controller);

        Label titleLabel = new Label("Audio Transcriptor");
        titleLabel.getStyleClass().add("title");

        TextArea editableTextArea = new TextArea();
        editableTextArea.setWrapText(true);
        editableTextArea.setEditable(false);
        editableTextArea.setPromptText("Il testo trascritto apparirà qui...");
        editableTextArea.getStyleClass().add("text-area");

        Button uploadButton = new Button("Carica File Audio");
        Button editButton = new Button("Modifica");
        Button saveAndExitButton = new Button("Salva e Esci");
        Button saveChangesButton = new Button("Salva Modifiche");
        Button cancelEditButton = new Button("Annulla");
        Button restoreOriginalButton = new Button("Ripristina Originale");

        // Stili per i pulsanti
        uploadButton.getStyleClass().add("button");
        editButton.getStyleClass().add("button");
        saveAndExitButton.getStyleClass().add("button");
        saveChangesButton.getStyleClass().add("button");
        cancelEditButton.getStyleClass().add("button");
        restoreOriginalButton.getStyleClass().add("button");

        // Inizialmente i pulsanti di modifica sono nascosti
        editButton.setVisible(false);
        saveAndExitButton.setVisible(false);
        saveChangesButton.setVisible(false);
        cancelEditButton.setVisible(false);
        restoreOriginalButton.setVisible(false);

        // Layout pulsanti
        HBox buttonBar = new HBox(20);
        buttonBar.getStyleClass().add("hbox");
        buttonBar.getChildren().addAll(editButton, saveAndExitButton);

        HBox editButtonsBar = new HBox(20);
        editButtonsBar.getStyleClass().add("hbox");
        editButtonsBar.getChildren().addAll(saveChangesButton, cancelEditButton, restoreOriginalButton);

        VBox root = new VBox(20, titleLabel, uploadButton, editableTextArea, buttonBar, editButtonsBar);
        root.getStyleClass().add("vbox");

        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        // Evento: Caricare un file audio
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona un file audio");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File Audio", "*.wav", "*.mp3"));

            File audioFile = fileChooser.showOpenDialog(primaryStage);
            if (audioFile != null) {
                String audioFilePath = audioFile.getAbsolutePath();
                System.out.println("File selezionato: " + audioFilePath);
                editableTextArea.setText("Trascrizione in corso...");

                new Thread(() -> {
                    boolean success = boundary.uploadAudio(audioFilePath);
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            originalText = controller.getTranscription().getText();
                            lastSavedText = originalText;
                            editableTextArea.setText(originalText);
                            editableTextArea.setEditable(false);
                            editButton.setVisible(true);
                            saveAndExitButton.setVisible(true);
                        } else {
                            editableTextArea.setText("Errore durante la trascrizione.");
                        }
                    });
                }).start();
            } else {
                editableTextArea.setText("Nessun file selezionato.");
            }
        });

        // Evento: Attivare la modalità di modifica
        editButton.setOnAction(event -> {
            editableTextArea.setEditable(true);
            saveChangesButton.setVisible(true);
            cancelEditButton.setVisible(true);
            restoreOriginalButton.setVisible(true);
            editButton.setVisible(false);
            saveAndExitButton.setVisible(false);
        });

        // Evento: Salvare le modifiche
        saveChangesButton.setOnAction(event -> {
            lastSavedText = editableTextArea.getText();
            System.out.println("✅ Testo modificato salvato.");
            editableTextArea.setEditable(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            restoreOriginalButton.setVisible(false);
        });

        // Evento: Annullare la modifica
        cancelEditButton.setOnAction(event -> {
            editableTextArea.setText(lastSavedText);
            System.out.println("🔄 Modifica annullata, ripristinato testo salvato.");
            editableTextArea.setEditable(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            restoreOriginalButton.setVisible(false);
        });

        // Evento: Ripristinare il testo originale
        restoreOriginalButton.setOnAction(event -> {
            editableTextArea.setText(originalText);
            System.out.println("↩️ Ripristinato il testo originale.");
        });

        // Evento: Salvare e uscire
        saveAndExitButton.setOnAction(event -> {
            System.out.println("💾 Testo salvato: " + lastSavedText);
            primaryStage.close();
        });

        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
