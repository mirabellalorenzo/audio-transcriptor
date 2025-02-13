package view.components;

import java.util.ArrayList;

import boundary.HomeBoundary;
import entity.Transcription;
import javafx.scene.control.Button;
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
    public void displaySummary(Transcription transcription, Stage primaryStage, BorderPane root) {
        VBox summaryBox = new VBox(15);
        summaryBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px; -fx-border-radius: 10px;");
        
        Label title = new Label("Resoconto della Trascrizione");
        title.setFont(new Font("Arial", 20));
        title.setTextFill(Color.web("#333"));

        HBox duration = createSummaryItem("clock-outline", "Durata Audio: " + transcription.getDuration() + " sec");
        HBox time = createSummaryItem("hourglass-outline", "Tempo di Trascrizione: " + (transcription.getProcessingTime() / 1000.0) + " sec");
        HBox words = createSummaryItem("document-text-outline", "Parole: " + transcription.getWordCount());
        HBox characters = createSummaryItem("text-outline", "Caratteri: " + transcription.getCharacterCount());

        Button backToNotesButton = new Button("Back to Notes");
        backToNotesButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-border-radius: 5px; -fx-padding: 8px 15px; -fx-font-size: 14px;");
        backToNotesButton.setOnAction(e -> {
            HomeBoundary homeBoundary = new HomeBoundary();
            NotesListComponent notesList = new NotesListComponent(homeBoundary, primaryStage, new ArrayList<>(), note -> {});
            root.setCenter(notesList);
        });


        summaryBox.getChildren().addAll(title, duration, time, words, characters, backToNotesButton);
        root.setCenter(summaryBox);
    }

    private HBox createSummaryItem(String iconName, String text) {
        ImageView icon = SvgToPngConverter.loadSvgAsImage(iconName, 24);
        Label label = new Label(text);
        label.setFont(new Font("Arial", 14));
        label.setTextFill(Color.web("#555"));

        HBox itemBox = new HBox(10, icon, label);
        itemBox.setStyle("-fx-alignment: center-left;");
        return itemBox;
    }
}
