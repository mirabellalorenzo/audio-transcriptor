package view;

import boundary.TranscriptionBoundary;
import control.TranscriptionController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
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


        // Unica TextArea per visualizzare e modificare il testo
        TextArea editableTextArea = new TextArea();
        editableTextArea.setWrapText(true);
        editableTextArea.setEditable(false); // Non modificabile all'inizio
        editableTextArea.setPromptText("Il testo trascritto apparirà qui...");

        // Pulsanti
        Button uploadButton = new Button("Carica File Audio");
        Button editButton = new Button("Modifica");
        Button saveAndExitButton = new Button("Salva e Esci");
        Button saveChangesButton = new Button("Salva Modifiche");
        Button cancelEditButton = new Button("Annulla");
        Button restoreOriginalButton = new Button("Ripristina Originale");


        // Configurazione pulsanti iniziale
        editButton.setVisible(false);
        saveAndExitButton.setVisible(false);
        saveChangesButton.setVisible(false);
        cancelEditButton.setVisible(false);
        restoreOriginalButton.setVisible(false);

        // Layout
        VBox root = new VBox(10, uploadButton, editableTextArea, editButton, saveAndExitButton, saveChangesButton, cancelEditButton, restoreOriginalButton);
        root.setStyle("-fx-padding: 10; -fx-alignment: center; -fx-spacing: 10;");
        Scene scene = new Scene(root, 600, 400);

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

                // Esegui la trascrizione in un thread separato
                new Thread(() -> {
                    boolean success = boundary.uploadAudio(audioFilePath);
                
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            // Aggiorna il testo originale dopo la trascrizione
                            originalText = controller.getTranscription().getText();
                            lastSavedText = originalText; // Inizialmente anche lastSavedText è il testo originale
                
                            editableTextArea.setText(originalText);
                            editableTextArea.setEditable(false);
                
                            // Mostra i pulsanti di revisione
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
        });

        // Salva Modifiche
        saveChangesButton.setOnAction(event -> {
            lastSavedText = editableTextArea.getText(); // Aggiorna l'ultima versione salvata
            editableTextArea.setEditable(false);
        
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
            restoreOriginalButton.setVisible(true);
        });
        
        // Annulla Modifiche
        cancelEditButton.setOnAction(event -> {
            editableTextArea.setText(lastSavedText); // Ripristina l'ultima versione salvata
            editableTextArea.setEditable(false);
        
            saveChangesButton.setVisible(false);
            cancelEditButton.setVisible(false);
            editButton.setVisible(true);
            saveAndExitButton.setVisible(true);
        });        

        // Salva e Esci
        saveAndExitButton.setOnAction(event -> {
            try {
                // Salva il testo modificato in un file
                java.nio.file.Files.writeString(
                    java.nio.file.Path.of("nota_finale.txt"),
                    editableTextArea.getText()
                );
                System.out.println("Nota salvata con successo!");

                // Mostra messaggio di completamento
                editableTextArea.setText("Nota salvata con successo!");
                editButton.setVisible(false);
                saveAndExitButton.setVisible(false);
            } catch (IOException e) {
                System.err.println("Errore durante il salvataggio della nota: " + e.getMessage());
            }
        });

        // Ripristina Testo Originale
        restoreOriginalButton.setOnAction(event -> {
            editableTextArea.setText(originalText); // Ripristina il testo originale
            editableTextArea.setEditable(false); // Non modificabile
        });           

        // Configura lo stage e avvia l'applicazione
        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
