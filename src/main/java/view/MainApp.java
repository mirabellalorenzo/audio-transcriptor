package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import control.SpeechToTextController;

import java.io.File;

public class MainApp extends Application {

    private TextArea transcriptionArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Audio Transcriptor");

        // Creazione interfaccia
        transcriptionArea = new TextArea();
        transcriptionArea.setEditable(false);

        Button loadAudioButton = new Button("Carica Audio");
        loadAudioButton.setOnAction(e -> handleLoadAudio(primaryStage));

        VBox root = new VBox(10, loadAudioButton, transcriptionArea);
        root.setStyle("-fx-padding: 10; -fx-spacing: 10;");

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleLoadAudio(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un file audio");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("File audio (*.wav, *.mp3)", "*.wav", "*.mp3"),
            new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );
        File file = fileChooser.showOpenDialog(stage);
    
        if (file != null) {
            transcriptionArea.setText("Trascrizione in corso...");
            
            // Esegui la trascrizione in un thread separato
            new Thread(() -> {
                String transcription = SpeechToTextController.transcribe(file.getAbsolutePath());
                
                // Aggiorna l'interfaccia nel thread JavaFX
                javafx.application.Platform.runLater(() -> transcriptionArea.setText(transcription));
            }).start();
        }
    }    
}
