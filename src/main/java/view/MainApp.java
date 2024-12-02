package view;

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
        // Layout principale
        VBox root = new VBox(10); // Spaziatura di 10px
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Etichetta per mostrare il percorso del file selezionato
        Label fileLabel = new Label("Nessun file selezionato");

        // Bottone per caricare il file
        Button loadButton = new Button("Carica Audio");

        // Area di testo per mostrare la trascrizione
        TextArea transcriptionArea = new TextArea();
        transcriptionArea.setPromptText("La trascrizione verrà mostrata qui...");
        transcriptionArea.setWrapText(true);
        transcriptionArea.setEditable(false); // Disabilita la modifica manuale

        // Bottone per iniziare la trascrizione
        Button transcribeButton = new Button("Trascrivi");
        transcribeButton.setDisable(true); // Disabilitato finché non si carica un file

        // Azione del bottone di caricamento
        loadButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona un file audio");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("File Audio", "*.mp3", "*.wav")
            );

            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                fileLabel.setText("File selezionato: " + selectedFile.getAbsolutePath());
                transcribeButton.setDisable(false); // Abilita il bottone "Trascrivi"
            } else {
                fileLabel.setText("Nessun file selezionato");
                transcribeButton.setDisable(true);
            }
        });

        // Azione del bottone di trascrizione
        transcribeButton.setOnAction(event -> {
            // Simula la trascrizione dell'audio
            String transcription = mockTranscription(fileLabel.getText());
            transcriptionArea.setText(transcription);
        });

        // Aggiungi i componenti al layout
        root.getChildren().addAll(fileLabel, loadButton, transcribeButton, transcriptionArea);

        // Crea la scena
        Scene scene = new Scene(root, 500, 400);

        // Configura la finestra principale
        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Funzione mock per simulare la trascrizione dell'audio
    private String mockTranscription(String filePath) {
        return "Trascrizione simulata per il file:\n" + filePath +
                "\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    }

    public static void main(String[] args) {
        launch(args);
    }
}
