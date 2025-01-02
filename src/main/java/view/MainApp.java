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

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Crea il controller e la boundary
        TranscriptionController controller = new TranscriptionController();
        TranscriptionBoundary boundary = new TranscriptionBoundary(controller);

        // Elementi della GUI
        Button uploadButton = new Button("Carica File Audio");
        TextArea transcriptionArea = new TextArea();
        transcriptionArea.setWrapText(true); // Abilita il word wrapping
        transcriptionArea.setEditable(false); // Non modificabile
        transcriptionArea.setPromptText("Il testo trascritto apparirà qui...");

        // Layout della GUI
        VBox root = new VBox(10, uploadButton, transcriptionArea);
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

                transcriptionArea.setText("Trascrizione in corso...");

                // Esegui la trascrizione in un thread separato
                new Thread(() -> {
                    boolean success = boundary.uploadAudio(audioFilePath);

                    // Aggiorna la GUI dopo la trascrizione
                    javafx.application.Platform.runLater(() -> {
                        if (success) {
                            transcriptionArea.setText(controller.getTranscription().getText());
                        } else {
                            transcriptionArea.setText("Errore durante la trascrizione.");
                        }
                    });
                }).start();
            } else {
                transcriptionArea.setText("Nessun file selezionato.");
                System.out.println("Nessun file selezionato.");
            }
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
