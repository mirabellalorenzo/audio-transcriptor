package view.gui2;

import java.util.ArrayList;

import boundary.HomeBoundary;
import boundary.TranscriptionBoundary;
import control.TranscriptionController;
import control.TranscriptionBean;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import view.components.FlatNotesListComponent;
import view.components.NavbarComponent;
import view.components.TranscriptionControlsComponent;
import view.components.TranscriptionEditorComponent;
import view.components.TranscriptionSummaryComponent;
import view.components.TranscriptionTitleComponent;

public class TranscriptionView2 {
    private TranscriptionBoundary boundary;
    private TranscriptionEditorComponent editorComponent;
    private TranscriptionControlsComponent controlsComponent;
    private TranscriptionSummaryComponent summaryComponent;
    private TranscriptionTitleComponent titleComponent;
    private Stage primaryStage;
    private BorderPane root;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        boundary = new TranscriptionBoundary(new TranscriptionController());

        HomeBoundary homeBoundary = new HomeBoundary();
        String userEmail = homeBoundary.getUserEmail();
        String userPhotoUrl = homeBoundary.getUserPhotoUrl();

        NavbarComponent navbar = new NavbarComponent(homeBoundary, primaryStage, userEmail, userPhotoUrl);

        editorComponent = new TranscriptionEditorComponent(boundary);
        controlsComponent = new TranscriptionControlsComponent(boundary, editorComponent, this::showTitlePage, primaryStage);
        summaryComponent = new TranscriptionSummaryComponent();
        titleComponent = new TranscriptionTitleComponent(boundary);

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
