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
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

public class SidebarComponent extends VBox {
    private final HomeBoundary boundary;
    private final Stage primaryStage;

    public SidebarComponent(HomeBoundary boundary, Stage primaryStage, String userEmail, String userPhotoUrl, List<Note> notes, NotesListComponent notesList) {
        this.boundary = boundary;
        this.primaryStage = primaryStage;

        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa; -fx-min-width: 250px;");

        // **Logo dell'App e Nome in HBox**
        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        logoContainer.setPadding(new Insets(10, 15, 10, 15));

        ImageView appLogo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        appLogo.setFitWidth(40);
        appLogo.setFitHeight(40);
        appLogo.setPreserveRatio(true);

        Label appNameLabel = new Label("AudioTranscriptor");
        appNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222;");

        logoContainer.getChildren().addAll(appLogo, appNameLabel);
        
        // **Sezione Menu**
        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(10, 0, 0, 0));
        
        // **Item Notes**
        HBox notesItem = createMenuItem("Notes", FontAwesomeSolid.STICKY_NOTE, () -> boundary.openToolView(primaryStage, "Notes"));
        
        // **Item Transcribe Audio**
        HBox transcribeItem = createMenuItem("Transcribe Audio", FontAwesomeSolid.MICROPHONE, () -> boundary.openToolView(primaryStage, "Transcribe Audio"));
        
        menuBox.getChildren().addAll(notesItem, transcribeItem);

        // **Pulsante di Logout con lo stesso stile del menu**
        
        HBox logoutItem = createMenuItem("Logout", FontAwesomeSolid.SIGN_OUT_ALT, () -> boundary.logout(primaryStage));
        VBox.setMargin(logoutItem, new Insets(0, 0, 20, 0)); // Aggiunge margine in basso


        // Posizionare il logout in fondo alla sidebar
        VBox spacer = new VBox();
        spacer.setMinHeight(Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(spacer, Priority.ALWAYS);


        getChildren().addAll(logoContainer, menuBox, spacer, logoutItem);
    }

    private HBox createMenuItem(String text, FontAwesomeSolid icon, Runnable action) {
        FontIcon menuIcon = new FontIcon(icon);
        menuIcon.setIconSize(22);
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