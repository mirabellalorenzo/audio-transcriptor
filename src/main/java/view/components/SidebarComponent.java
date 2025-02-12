package view.components;

import boundary.HomeBoundary;
import entity.Note;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.List;

public class SidebarComponent extends VBox {
    private final HomeBoundary boundary;
    private final Stage primaryStage;

    public SidebarComponent(HomeBoundary boundary, Stage primaryStage, String userEmail, String userPhotoUrl, List<Note> notes, NotesListComponent notesList) {
        this.boundary = boundary;
        this.primaryStage = primaryStage;

        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa; -fx-min-width: 250px;");

        // **Immagine Profilo**
        ImageView profileImage = new ImageView(new Image(userPhotoUrl));
        profileImage.setFitWidth(50);
        profileImage.setFitHeight(50);
        profileImage.setPreserveRatio(true);
        profileImage.setStyle("-fx-background-radius: 50%; -fx-border-radius: 50%;");

        // **Email Utente**
        Label emailLabel = new Label(userEmail);
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // **Container Utente**
        VBox userBox = new VBox(10, profileImage, emailLabel);
        userBox.setAlignment(Pos.CENTER);
        
        // **Sezione Menu**
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(10, 0, 0, 0));
        
        // **Item Notes**
        HBox notesItem = createMenuItem("Notes", FontAwesomeSolid.STICKY_NOTE, () -> boundary.openToolView(primaryStage, "Notes"));
        
        // **Item Transcribe Audio**
        HBox transcribeItem = createMenuItem("Transcribe Audio", FontAwesomeSolid.MICROPHONE, () -> boundary.openToolView(primaryStage, "Transcribe Audio"));
        
        menuBox.getChildren().addAll(notesItem, transcribeItem);
        getChildren().addAll(userBox, menuBox);
    }

    private HBox createMenuItem(String text, FontAwesomeSolid icon, Runnable action) {
        FontIcon menuIcon = new FontIcon(icon);
        menuIcon.setIconSize(18);
        menuIcon.setStyle("-fx-icon-color: #555;");
        
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #222;");
        
        HBox item = new HBox(10, menuIcon, label);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-radius: 8px; -fx-cursor: hand;");
        
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8px;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-radius: 8px;"));
        item.setOnMouseClicked(e -> action.run());
        
        return item;
    }
}