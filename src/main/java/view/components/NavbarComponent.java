package view.components;

import boundary.HomeBoundary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import util.SvgToPngConverter;

public class NavbarComponent extends HBox {
    private final HomeBoundary boundary;
    private final Stage primaryStage;

    public NavbarComponent(HomeBoundary boundary, Stage primaryStage, String userEmail, String userPhotoUrl) {
        this.boundary = boundary;
        this.primaryStage = primaryStage;

        setSpacing(20);
        setPadding(new Insets(15, 30, 15, 30)); // Maggiore padding ai lati
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: #f8f9fa; -fx-min-height: 60px;");

        // **Logo dell'App e Nome**
        ImageView appLogo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        appLogo.setFitWidth(40);
        appLogo.setFitHeight(40);
        appLogo.setPreserveRatio(true);

        Label appNameLabel = new Label("AudioTranscriptor");
        appNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222;");

        HBox logoContainer = new HBox(10, appLogo, appNameLabel);
        logoContainer.setAlignment(Pos.CENTER_LEFT);

        // **Strumenti al centro**
        HBox notesItem = createNavItem("Notes", "document-text-outline", () -> boundary.openPageView(primaryStage, "Notes"));
        HBox transcribeItem = createNavItem("Transcribe Audio", "mic-outline", () -> boundary.openPageView(primaryStage, "Transcribe Audio"));

        HBox toolsContainer = new HBox(30, notesItem, transcribeItem);
        toolsContainer.setAlignment(Pos.CENTER);

        // **Logout, Email e Immagine Profilo a Destra**
        HBox logoutItem = createNavItem("Logout", "log-out-outline", () -> boundary.logout(primaryStage));

        Label emailLabel = new Label(userEmail);
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

        // **Immagine del profilo con ritaglio circolare**
        ImageView profileImage = new ImageView(new Image(userPhotoUrl, 40, 40, true, true));
        profileImage.setFitWidth(40);
        profileImage.setFitHeight(40);
        profileImage.setPreserveRatio(true);

        // **Applica un ritaglio circolare per garantire la forma corretta**
        Circle clip = new Circle(20, 20, 20); // X, Y, Raggio (stesso raggio della metà dell'immagine)
        profileImage.setClip(clip);

        // **Bordo intorno all'immagine (simula effetto cerchio)**
        StackPane profileImageContainer = new StackPane();
        profileImageContainer.setStyle(
            "-fx-background-color: white; " +  // Sfondo bianco per il bordo
            "-fx-border-color: #ccc; " + // Bordo grigio chiaro
            "-fx-border-width: 1px; " + 
            "-fx-border-radius: 50%; " +
            "-fx-background-radius: 50%; " + // Assicura il bordo circolare
            "-fx-padding: 3px;" // Padding per distanziare immagine dal bordo
        );
        profileImageContainer.getChildren().add(profileImage);

        
        HBox profileContainer = new HBox(10, emailLabel, profileImageContainer);
        profileContainer.setAlignment(Pos.CENTER_RIGHT);

        HBox rightContainer = new HBox(20, logoutItem, profileContainer);
        rightContainer.setAlignment(Pos.CENTER_RIGHT);

        // **Riempitivo centrale per centrare i tool**
        Region spacerLeft = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        getChildren().addAll(logoContainer, spacerLeft, toolsContainer, spacerRight, rightContainer);
    }

    private HBox createNavItem(String text, String iconName, Runnable action) {
        ImageView icon = SvgToPngConverter.loadSvgAsImage(iconName, 24);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #222; -fx-cursor: hand;");

        HBox item = new HBox(10, icon, label);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-radius: 8px; -fx-cursor: hand;");

        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8px;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-radius: 8px;"));
        item.setOnMouseClicked(e -> action.run());

        return item;
    }
}
