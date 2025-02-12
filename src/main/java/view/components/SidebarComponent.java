package view.components;

import boundary.HomeBoundary;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SidebarComponent extends VBox {
    private final HomeBoundary boundary;
    private final Stage primaryStage;
    private static final Logger logger = LoggerFactory.getLogger(SidebarComponent.class);

    public SidebarComponent(HomeBoundary boundary, Stage primaryStage, String userEmail, String userPhotoUrl) {
        this.boundary = boundary;
        this.primaryStage = primaryStage;

        setSpacing(20);
        setStyle("-fx-padding: 20; -fx-background-color: #f8f9fa; -fx-min-width: 220px;");

        // **Immagine Profilo**
        ImageView profileImage = new ImageView(new Image(userPhotoUrl));
        profileImage.setFitWidth(60);
        profileImage.setFitHeight(60);
        profileImage.setPreserveRatio(true);
        profileImage.setStyle("-fx-background-radius: 50%; -fx-border-radius: 50%;"); // 🔥 Arrotondato

        // **Email Utente**
        Label emailLabel = new Label(userEmail);
        emailLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        // **Pulsante "New Note"**
        Button newNoteButton = new Button("New Note");
        newNoteButton.setStyle(
            "-fx-background-color: #0078d7; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12px 20px; " +
            "-fx-min-width: 180px; " +
            "-fx-font-size: 14px; " +
            "-fx-border-radius: 8px; " +
            "-fx-cursor: hand;" // 🔥 Cursor Pointer
        );

        newNoteButton.setOnAction(e -> {
            logger.info("New Note button clicked"); // ✅ Debug log
            boundary.createNewNote();
        });

        // **Pulsante "Transcribe Audio"**
        Button transcribeButton = new Button("Transcribe Audio");
        transcribeButton.setStyle(
            "-fx-background-color: #28a745; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 12px 20px; " +
            "-fx-min-width: 180px; " +
            "-fx-font-size: 14px; " +
            "-fx-border-radius: 8px; " +
            "-fx-cursor: hand;" // 🔥 Cursor Pointer
        );

        transcribeButton.setOnAction(e -> {
            logger.info("Transcribe button clicked"); // ✅ Debug log
            boundary.openToolView(primaryStage, "Transcribe Audio");
        });

        getChildren().addAll(profileImage, emailLabel, newNoteButton, transcribeButton);
    }
}
