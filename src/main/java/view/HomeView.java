package view;

import boundary.HomeBoundary;
import entity.Note;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HomeView {
    private static final Logger logger = LoggerFactory.getLogger(HomeView.class);

    public void start(Stage primaryStage) {
        HomeBoundary boundary = new HomeBoundary();

        String email = boundary.getUserEmail();
        if (email == null || email.isEmpty()) {
            email = "Unknown User";
        }
        String photoUrl = boundary.getUserPhotoUrl();
        List<Note> savedNotes = boundary.getSavedNotes();

        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-padding: 0;");
        
        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-background-color: #f8f9fa; -fx-border-width: 0 0 1 0; -fx-border-color: #ddd; -fx-alignment: center;");


        // Immagine profilo 
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
        
        HBox.setHgrow(profileImage, Priority.ALWAYS);
        HBox.setHgrow(logoutButton, Priority.ALWAYS);
        topBar.getChildren().addAll(profileImage, emailLabel, logoutButton);
        root.setTop(topBar);

        Separator separator = new Separator();
        separator.setStyle("-fx-border-color: #000000; -fx-padding: 10;");
        root.setCenter(separator);

        VBox centerContent = new VBox(20);
        centerContent.setAlignment(Pos.TOP_CENTER);


        Label titleLabel = new Label("Hi, click here to transcribe an audio");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #444; -fx-padding: 30 0 5 0;");
        titleLabel.setAlignment(Pos.CENTER);

        GridPane centerPanel = new GridPane();
        centerPanel.setHgap(20);
        centerPanel.setVgap(20);
        centerPanel.setAlignment(Pos.CENTER);

        Button transcribeButton = new Button("Transcribe Audio");
        transcribeButton.getStyleClass().add("card-button");
        transcribeButton.setMaxWidth(150);
        transcribeButton.setOnAction(e -> boundary.openToolView(primaryStage, "Transcribe Audio"));

        centerPanel.add(transcribeButton, 0, 0);


        // Sezione note
        VBox notesContainer = new VBox(10);
        notesContainer.setAlignment(Pos.TOP_CENTER);
        notesContainer.setStyle("-fx-padding: 20; -fx-max-width: 400px;");

        Label notesTitle = new Label("Your saved notes:");
        notesTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 0 5 0;");

        notesContainer.getChildren().add(notesTitle);

        if (savedNotes.isEmpty()) {
            notesContainer.getChildren().add(new Label("You don't have saved notes"));
        } else {
            for (Note note : savedNotes) {
                VBox noteBox = new VBox(5); 
                noteBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 10; -fx-spacing: 5;");

                Label noteTitle = new Label(note.getTitle());
                noteTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

                String previewText = note.getContent().split("\n").length > 2 ? 
                                    note.getContent().split("\n")[0] + "\n" + note.getContent().split("\n")[1] + "..." : 
                                    note.getContent();

                Label notePreview = new Label(previewText);
                notePreview.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

                noteBox.setOnMouseEntered(e -> noteBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #bbb; -fx-border-radius: 5px; -fx-padding: 10; -fx-spacing: 5;"));
                noteBox.setOnMouseExited(e -> noteBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-width: 1px; -fx-border-radius: 5px; -fx-padding: 10; -fx-spacing: 5;"));

                noteBox.getChildren().addAll(noteTitle, notePreview);

                
                noteBox.setOnMouseClicked(e -> {
                    Stage modalStage = new Stage();
                    modalStage.setTitle("Edit Note");
            
                    TextArea modalTextArea = new TextArea(note.getContent());
                    modalTextArea.setWrapText(true);
                    modalTextArea.getStyleClass().add("text-area"); 
            
                    Button closeButton = new Button("Close");
                    closeButton.getStyleClass().add("button-secondary");
                    closeButton.setOnAction(ev -> modalStage.close());

                    Button saveButton = new Button("Save");
                    saveButton.getStyleClass().add("button-primary"); 
                    saveButton.setOnAction(ev -> {
                        note.setContent(modalTextArea.getText());
                        boundary.updateNote();
                        logger.info("Note updated: {}", note.getTitle());
                        modalStage.close(); 
                    });
            
                    // Layout dei pulsanti
                    HBox buttonBox = new HBox(15, closeButton, saveButton);
                    buttonBox.setAlignment(Pos.CENTER);
            
                    // Layout principale del modal
                    VBox modalLayout = new VBox(15, modalTextArea, buttonBox);
                    modalLayout.setAlignment(Pos.CENTER);
                    modalLayout.setStyle("-fx-padding: 20; -fx-background-color: #ffffff; -fx-border-radius: 10px; -fx-border-color: #cccccc;");
            
                    Scene modalScene = new Scene(modalLayout, 400, 300);
                    modalScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm()); // Applica il CSS
                    modalStage.setScene(modalScene);
                    modalStage.showAndWait();
                });
            
                notesContainer.getChildren().add(noteBox);
            }            
        }

        centerContent.getChildren().addAll(titleLabel, centerPanel, notesContainer);

        root.setCenter(centerContent);

        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
