package view;

import boundary.TranscriptionBoundary;
import control.HomeController;
import control.TranscriptionController;
import entity.Transcription;
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
        System.out.println("TranscriptionView avviata.");
        TranscriptionController controller = new TranscriptionController();
        TranscriptionBoundary boundary = new TranscriptionBoundary(controller);

        Label titleLabel = new Label("Audio Transcriptor");
        titleLabel.getStyleClass().add("title");

        TextArea editableTextArea = new TextArea();
        editableTextArea.setWrapText(true);
        editableTextArea.setEditable(false);
        editableTextArea.setPromptText("Il testo trascritto apparirà qui...");
        editableTextArea.getStyleClass().add("text-area");
        editableTextArea.setPrefHeight(250); // Altezza preferita
        editableTextArea.setMaxHeight(Double.MAX_VALUE); // Permette il ridimensionamento
        editableTextArea.setMinHeight(150); // Altezza minima

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

        // Etichette per il resoconto
        Label summaryLabel = new Label("📊 Resoconto della Trascrizione:");
        Label durationLabel = new Label();
        Label timeLabel = new Label();
        Label wordsLabel = new Label();
        Label charsLabel = new Label();

        // Layout per il resoconto
        VBox summaryBox = new VBox(10, summaryLabel, durationLabel, timeLabel, wordsLabel, charsLabel);
        summaryBox.getStyleClass().add("summary-box");
        summaryBox.setVisible(false); // Inizialmente nascosto

        // Layout pulsanti
        HBox buttonBar = new HBox(20);
        buttonBar.getStyleClass().add("hbox");
        buttonBar.getChildren().addAll(editButton, saveAndExitButton);

        HBox editButtonsBar = new HBox(20);
        editButtonsBar.getStyleClass().add("hbox");
        editButtonsBar.getChildren().addAll(saveChangesButton, cancelEditButton, restoreOriginalButton);

        // Pulsante per tornare alla Home
        Button backButton = new Button("← Back to Home");
        backButton.getStyleClass().add("button");
        HomeController homeController = new HomeController();
        backButton.setOnAction(event -> homeController.openHome(primaryStage));

        HBox backButtonBar = new HBox(backButton);
        backButtonBar.getStyleClass().add("back-button-bar");

        VBox root = new VBox(20, backButtonBar, titleLabel, uploadButton, editableTextArea, buttonBar, editButtonsBar, summaryBox);
        root.getStyleClass().add("vbox");

        Scene scene = new Scene(root, 700, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        // Caricare un file audio
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

                            // Recupera i dati della trascrizione
                            Transcription t = controller.getTranscription();
                            durationLabel.setText("🎵 Durata Audio: " + t.getDuration() + " sec");
                            timeLabel.setText("⏳ Tempo di Trascrizione: " + (t.getProcessingTime() / 1000.0) + " sec");
                            wordsLabel.setText("📝 Parole: " + t.getWordCount());
                            charsLabel.setText("🔡 Caratteri: " + t.getCharacterCount());

                        } else {
                            editableTextArea.setText("Errore durante la trascrizione.");
                        }
                    });
                }).start();
            } else {
                editableTextArea.setText("Nessun file selezionato.");
            }
        });

        // Attivare la modalità di modifica
        editButton.setOnAction(event -> {
            editableTextArea.setEditable(true);
            saveChangesButton.setVisible(true);
            cancelEditButton.setVisible(true);
            restoreOriginalButton.setVisible(true);
            editButton.setVisible(false);
            saveAndExitButton.setVisible(false);
        });

        // Salvare le modifiche
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

        // Annullare la modifica
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

        // Ripristinare il testo originale
        restoreOriginalButton.setOnAction(event -> {
            editableTextArea.setText(originalText);
            System.out.println("↩️ Ripristinato il testo originale.");
        });

        // Salvare e uscire
        saveAndExitButton.setOnAction(event -> {
            boolean saved = boundary.saveTranscription(primaryStage);
            
            if (saved) {
                showSummaryDialog(primaryStage, controller.getTranscription());
            } else {
                System.err.println("❌ Errore nel salvataggio della trascrizione.");
            }
        });
        
        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showSummaryDialog(Stage primaryStage, Transcription transcription) {
        Alert summaryDialog = new Alert(Alert.AlertType.INFORMATION);
        summaryDialog.setTitle("📊 Resoconto della Trascrizione");
        summaryDialog.setHeaderText("Trascrizione completata con successo!");
        
        // Testo del resoconto
        String summaryText = String.format(
            "🎵 Durata Audio: %d sec\n" +
            "⏳ Tempo di Trascrizione: %.2f sec\n" +
            "📝 Parole: %d\n" +
            "🔡 Caratteri: %d",
            transcription.getDuration(),
            transcription.getProcessingTime() / 1000.0,
            transcription.getWordCount(),
            transcription.getCharacterCount()
        );
        
        summaryDialog.setContentText(summaryText);
    
        // Pulsante "Torna alla Home"
        ButtonType backToHomeButton = new ButtonType("Torna alla Home");
        summaryDialog.getButtonTypes().setAll(backToHomeButton);
    
        summaryDialog.showAndWait(); // Mostra la finestra di dialogo
        
        // Dopo che l'utente chiude il resoconto, torna alla home
        HomeController homeController = new HomeController();
        homeController.openHome(primaryStage);
    }    
}
