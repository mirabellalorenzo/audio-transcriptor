package view;

import boundary.HomeBoundary;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class HomeView {

    public void start(Stage primaryStage) {
        HomeBoundary boundary = new HomeBoundary();

        String email = boundary.getUserEmail();
        String photoUrl = boundary.getUserPhotoUrl();
        
        BorderPane root = new BorderPane();
        
        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Immagine del profilo 
        ImageView profileImage = new ImageView(new Image(photoUrl));
        profileImage.setFitWidth(50); 
        profileImage.setFitHeight(50); 
        profileImage.setPreserveRatio(true); 
        profileImage.setSmooth(true);

        Circle clip = new Circle(25, 25, 25); 
        profileImage.setClip(clip);

        StackPane profileContainer = new StackPane(profileImage);
        profileContainer.setStyle("-fx-border-color: #0078d7; -fx-border-width: 2px; -fx-background-radius: 50%; -fx-border-radius: 50%;");
        profileContainer.setPrefSize(50, 50); 

        // User Email
        Label emailLabel = new Label(email);
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Logout Button
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> boundary.logout(primaryStage));
        logoutButton.getStyleClass().add("button");
        
        // Navbar Alignment
        HBox.setHgrow(profileImage, Priority.ALWAYS);
        HBox.setHgrow(logoutButton, Priority.ALWAYS);
        topBar.getChildren().addAll(profileImage, emailLabel, logoutButton);
        root.setTop(topBar);

        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-border-color: #000000; -fx-padding: 10;");
        root.setCenter(separator);

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Hi, choose an action to start");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        titleLabel.setAlignment(Pos.CENTER);

        GridPane centerPanel = new GridPane();
        centerPanel.setHgap(20);
        centerPanel.setVgap(20);
        centerPanel.setAlignment(Pos.CENTER);

        String[] toolNames = {"Transcribe Audio", "Upload File", "Tool 3", "Tool 4"};

        for (int i = 0; i < 4; i++) {
            final int index = i;

            Button toolButton = new Button(toolNames[index]);
            toolButton.getStyleClass().add("card-button");
            toolButton.setMaxWidth(150);

            int row = index / 2;
            int col = index % 2;

            toolButton.setOnAction(e -> boundary.openToolView(primaryStage, toolNames[index]));
            centerPanel.add(toolButton, col, row);
        }

        centerContent.getChildren().addAll(titleLabel, centerPanel);
        root.setCenter(centerContent);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
