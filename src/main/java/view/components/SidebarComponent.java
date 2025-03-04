package view.components;

import boundary.HomeBoundary;
import control.HomeController;
import control.NoteBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.SvgToPngConverter;

import java.util.List;

public class SidebarComponent extends VBox {
    private final HomeController homeController;
    private final Stage primaryStage;

    public SidebarComponent(HomeController homeController, Stage primaryStage, String userEmail, String userPhotoUrl, List<NoteBean> notes, NotesListComponent notesList) {
        this.homeController = homeController;
        this.primaryStage = primaryStage;

        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f8f9fa; -fx-min-width: 250px;");

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

        VBox menuBox = new VBox(15);
        menuBox.setPadding(new Insets(10, 0, 0, 0));

        HBox notesItem = createMenuItem("Notes", "document-text-outline", () -> homeController.openPageView(primaryStage, "Notes"));

        HBox transcribeItem = createMenuItem("Transcribe Audio", "mic-outline", () -> homeController.openPageView(primaryStage, "Transcribe Audio"));

        menuBox.getChildren().addAll(notesItem, transcribeItem);

        HBox logoutItem = createMenuItem("Logout", "log-out-outline", () -> homeController.logout(primaryStage));
        VBox.setMargin(logoutItem, new Insets(0, 0, 20, 0));

        VBox spacer = new VBox();
        spacer.setMinHeight(Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(logoContainer, menuBox, spacer, logoutItem);
    }

    private HBox createMenuItem(String text, String iconName, Runnable action) {
        ImageView icon = SvgToPngConverter.loadSvgAsImage(iconName, 24);

        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-text-fill: #222; -fx-cursor: hand;");

        HBox item = new HBox(10, icon, label);
        item.setPadding(new Insets(10, 15, 10, 15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-radius: 8px; -fx-cursor: hand;");

        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: #e9ecef; -fx-background-radius: 8px; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-radius: 8px; -fx-cursor: hand;"));
        item.setOnMouseClicked(e -> action.run());

        return item;
    }
}
