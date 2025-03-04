package view.components;

import config.AppConfig;
import control.TranscriptionBean;
import control.TranscriptionController;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TranscriptionTitleComponent {
    private final TranscriptionController controller;

    public TranscriptionTitleComponent(TranscriptionController controller) {
        this.controller = controller;
    }

    public void displayTitleInput(Stage primaryStage, BorderPane root) {
        VBox inputBox = new VBox(15);
        inputBox.setStyle(
            "-fx-background-color: #F5F5F5; " +
            "-fx-padding: 25px; " +
            "-fx-background-radius: 30px; " +
            "-fx-background-insets: 0;"
        );
        inputBox.setMaxWidth(400);
        inputBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Enter a title");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #222;");

        TextField titleField = new TextField();
        titleField.setPromptText("New Note");
        titleField.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 10px; " +
            "-fx-border-radius: 8px; " +
            "-fx-border-color: #E0E0E0; " +
            "-fx-background-radius: 8px; " +
            "-fx-background-color: white;"
        );
        titleField.setMaxWidth(300);

        CustomButtonComponent continueButton = new CustomButtonComponent("Continue", CustomButtonComponent.ButtonType.PRIMARY);
        continueButton.setOnAction(e -> {
            String title = titleField.getText().trim();

            if (!title.isEmpty()) {
                TranscriptionBean transcriptionBean = controller.getTranscription();

                if (transcriptionBean == null) {
                    System.err.println("Errore: nessuna trascrizione disponibile per assegnare un titolo.");
                    return;
                }

                transcriptionBean.setTitle(title);

                boolean saved = controller.saveTranscription(transcriptionBean);
                if (saved) {
                    TranscriptionSummaryComponent summaryComponent = new TranscriptionSummaryComponent(new AppConfig());
                    summaryComponent.displaySummary(transcriptionBean, primaryStage, root);
                } else {
                    System.err.println("Errore: impossibile salvare la trascrizione.");
                }
            }
        });

        inputBox.getChildren().addAll(titleLabel, titleField, continueButton);

        VBox centeredBox = new VBox(20, inputBox);
        centeredBox.setStyle("-fx-background-color: white; -fx-padding: 20px;");
        centeredBox.setAlignment(Pos.CENTER);
        centeredBox.setMaxWidth(Double.MAX_VALUE);

        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(centeredBox);

        root.setCenter(wrapper);
    }
}
