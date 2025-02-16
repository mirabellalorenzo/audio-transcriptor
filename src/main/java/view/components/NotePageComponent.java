package view.components;

import entity.Note;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NotePageComponent {
    
    public void showNoteDetail(Stage parentStage, Note note) {
        VBox modalBox = new VBox(15);
        modalBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 20px; " +
            "-fx-border-radius: 15px; " +
            "-fx-background-radius: 15px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );
        modalBox.setAlignment(Pos.CENTER);
        modalBox.setMaxWidth(500);
    
        Label titleLabel = new Label(note.getTitle());
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label contentLabel = new Label(note.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
    
        Button closeButton = new Button("Chiudi");
        closeButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-padding: 8px 16px; -fx-border-radius: 8px;");
        closeButton.setCursor(Cursor.HAND);
        closeButton.setOnAction(e -> hideModal(parentStage));
    
        VBox contentBox = new VBox(10, titleLabel, contentLabel, closeButton);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_LEFT);
    
        modalBox.getChildren().addAll(contentBox);
    
        VBox overlay = new VBox();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        overlay.setAlignment(Pos.CENTER);
        overlay.getChildren().add(modalBox);

        // **Controlla se il root è StackPane**
        if (parentStage.getScene().getRoot() instanceof StackPane) {
            StackPane root = (StackPane) parentStage.getScene().getRoot();
            root.getChildren().add(overlay);
        }
        
        // **Chiudi il modal cliccando fuori**
        overlay.setOnMouseClicked(e -> hideModal(parentStage));
    }
    
    private void hideModal(Stage parentStage) {
        if (parentStage.getScene().getRoot() instanceof StackPane) {
            StackPane root = (StackPane) parentStage.getScene().getRoot();
            root.getChildren().remove(root.getChildren().size() - 1);
        }
    }
}
