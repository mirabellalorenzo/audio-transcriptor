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
import java.io.IOException;

public class MainApp extends Application {
    String lastSavedText = ""; // Memorizza l'ultima versione salvata del testo
    String originalText = ""; // Memorizza il testo originale trascritto

    @Override
    public void start(Stage primaryStage) {
        // Crea il controller e la boundary
        TranscriptionController controller = new TranscriptionController();
        TranscriptionBoundary boundary = new TranscriptionBoundary(controller);

        // Intestazione
        Label titleLabel = new Label("Audio Transcriptor");
        titleLabel.getStyleClass().add("title");

        // Unica TextArea per visualizzare e modificare il testo
        TextArea editableTextArea = new TextArea();
        editableTextArea.setWrapText(true);
        editableTextArea.setEditable(false);
        editableTextArea.setPromptText("Il testo trascritto apparirà qui...");
        editableTextArea.getStyleClass().add("text-area");

        // Pulsanti
        Button uploadButton = new Button("Carica File Audio");
        Button editButton = new Button("Modifica");
        Button saveAndExitButton = new Button("Salva e Esci");
        Button saveChangesButton = new Button("Salva Modifiche");
        Button cancelEditButton = new Button("Annulla");
        Button restoreOriginalButton = new Button("Ripristina Originale");

        uploadButton.getStyleClass().add("button");
        editButton.getStyleClass().add("button");
        saveAndExitButton.getStyleClass().add("button");
        saveChangesButton.getStyleClass().add("button");
        cancelEditButton.getStyleClass().add("button");
        restoreOriginalButton.getStyleClass().add("button");

        // Configurazione pulsanti iniziale
        editButton.setVisible(false);
        saveAndExitButton.setVisible(false);
        saveChangesButton.setVisible(false);
        cancelEditButton.setVisible(false);
        restoreOriginalButton.setVisible(false);

        // Layout Pulsanti
        HBox buttonBar = new HBox(20);
        buttonBar.getStyleClass().add("hbox");
        buttonBar.getChildren().addAll(editButton, saveAndExitButton);

        // Layout Principale
        VBox root = new VBox(20, titleLabel, uploadButton, editableTextArea, buttonBar);
        root.getStyleClass().add("vbox");

        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        // Configura il pulsante di caricamento
        uploadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona un file audio");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File Audio", "*.wav", "*.mp3")
            );

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
                System.out.println("Nessun file selezionato.");
            }
        });

        // Modifica Testo
        editButton.setOnAction(event -> {
            editableTextArea.setEditable(true);
            saveChangesButton.setVisible(true);
            cancelEditButton.setVisible(true);
            editButton.setVisible(false);
            saveAndExitButton.setVisible(false);
        
            buttonBar.getChildren().setAll(saveChangesButton, cancelEditButton);
        });

        // Salva Modifiche
        saveChangesButton.setOnAction(event -> {
            lastSavedText = editableTextArea.getText();
            editableTextArea.setEditable(false);
        
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
        
            buttonBar.getChildren().setAll(editButton, saveAndExitButton);
        });

        // Annulla Modifiche
        cancelEditButton.setOnAction(event -> {
            editableTextArea.setText(lastSavedText);
            editableTextArea.setEditable(false);
        
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
        
            buttonBar.getChildren().setAll(editButton, saveAndExitButton);
        });

        // Salva e Esci
        saveAndExitButton.setOnAction(event -> {
            try {
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("nota_finale.txt"),
                    editableTextArea.getText()
                );
                System.out.println("Nota salvata con successo!");

                editableTextArea.setText("Nota salvata con successo!");
                editButton.setVisible(false);
                saveAndExitButton.setVisible(false);
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio della nota: " + e.getMessage());
            }
        });

        // Ripristina Testo Originale
        restoreOriginalButton.setOnAction(event -> {
            editableTextArea.setText(originalText);
            editableTextArea.setEditable(false);
        });

        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
