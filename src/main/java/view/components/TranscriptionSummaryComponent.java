package view.components;

import control.HomeController;
import control.TranscriptionBean;
import config.AppConfig;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import util.SvgToPngConverter;

public class TranscriptionSummaryComponent {
    private final HomeController homeController;

    public TranscriptionSummaryComponent(AppConfig appConfig) {
        this.homeController = new HomeController(appConfig);
    }

    public void displaySummary(TranscriptionBean transcription, Stage primaryStage, BorderPane root) {
        VBox infoBox = new VBox(15);
        infoBox.setStyle(
            "-fx-background-color: #F5F5F5; " +
            "-fx-padding: 25px; " +
            "-fx-background-radius: 30px; " +
            "-fx-background-insets: 0;"
        );        
        
        infoBox.setMaxWidth(400);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Transcription Summary");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #222;");

        HBox duration = createSummaryItem("time-outline", "Audio Duration: " + transcription.getDuration() + " sec");
        HBox time = createSummaryItem("hourglass-outline", "Processing Time: " + (transcription.getProcessingTime() / 1000.0) + " sec");
        HBox words = createSummaryItem("document-text-outline", "Number of Words: " + transcription.getWordCount());
        HBox characters = createSummaryItem("text-outline", "Number of Characters: " + transcription.getCharacterCount());

        CustomButtonComponent backToNotesButton = new CustomButtonComponent("Back to Notes", CustomButtonComponent.ButtonType.PRIMARY);
        backToNotesButton.setOnAction(e -> homeController.openPageView(primaryStage, "Notes"));

        infoBox.getChildren().addAll(duration, time, words, characters);

        VBox centeredBox = new VBox(20, titleLabel, infoBox, backToNotesButton);
        centeredBox.setStyle("-fx-background-color: white; -fx-padding: 20px;");
        centeredBox.setAlignment(Pos.CENTER);
        centeredBox.setMaxWidth(Double.MAX_VALUE);

        BorderPane wrapper = new BorderPane();
        wrapper.setCenter(centeredBox);

        root.setCenter(wrapper);
    }

    private HBox createSummaryItem(String iconName, String text) {
        ImageView icon = SvgToPngConverter.loadSvgAsImage(iconName, 26);
        Label label = new Label(text);
        label.setFont(new Font("Arial", 16));
        label.setTextFill(Color.web("#444"));

        HBox itemBox = new HBox(10, icon, label);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        return itemBox;
    }
}
