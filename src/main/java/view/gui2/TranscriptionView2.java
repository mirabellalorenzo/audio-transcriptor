package view.gui2;

import boundary.HomeBoundary;
import boundary.TranscriptionBoundary;
import control.HomeController;
import control.TranscriptionController;
import control.TranscriptionBean;
import config.AppConfig;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import view.components.NavbarComponent;
import view.components.TranscriptionControlsComponent;
import view.components.TranscriptionEditorComponent;
import view.components.TranscriptionSummaryComponent;
import view.components.TranscriptionTitleComponent;

public class TranscriptionView2 {
    private final AppConfig appConfig;
    private TranscriptionBoundary boundary;
    private TranscriptionTitleComponent titleComponent;
    private Stage primaryStage;
    private BorderPane root;

    public TranscriptionView2(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        HomeController homeController = new HomeController(appConfig);
        TranscriptionController transcriptionController = new TranscriptionController(appConfig);
        String userEmail = homeController.getUserEmail();
        String userPhotoUrl = homeController.getUserPhotoUrl();

        NavbarComponent navbar = new NavbarComponent(homeController, primaryStage, userEmail, userPhotoUrl);

        TranscriptionEditorComponent editorComponent = new TranscriptionEditorComponent(transcriptionController);
        TranscriptionControlsComponent controlsComponent = new TranscriptionControlsComponent(transcriptionController, editorComponent, this::showTitlePage, primaryStage);
        TranscriptionSummaryComponent summaryComponent = new TranscriptionSummaryComponent(appConfig);
        titleComponent = new TranscriptionTitleComponent(transcriptionController);

        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        VBox centerBox = new VBox(10, topSpacer, editorComponent, controlsComponent, bottomSpacer);
        centerBox.setAlignment(Pos.CENTER);

        root = new BorderPane();
        root.setTop(navbar);
        root.setCenter(centerBox);
        root.setStyle("-fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showTitlePage(TranscriptionBean transcription) {
        titleComponent.displayTitleInput(primaryStage, root);
    }
}
