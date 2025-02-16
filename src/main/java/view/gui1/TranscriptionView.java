package view.gui1;

import java.util.ArrayList;

import boundary.HomeBoundary;
import boundary.TranscriptionBoundary;
import control.TranscriptionController;
import entity.Transcription;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.components.NotesListComponent;
import view.components.SidebarComponent;
import view.components.TranscriptionControlsComponent;
import view.components.TranscriptionEditorComponent;
import view.components.TranscriptionSummaryComponent;
import view.components.TranscriptionTitleComponent;

public class TranscriptionView {
    private TranscriptionBoundary boundary;
    private TranscriptionEditorComponent editorComponent;
    private TranscriptionControlsComponent controlsComponent;
    private TranscriptionSummaryComponent summaryComponent;
    private TranscriptionTitleComponent titleComponent;
    private SidebarComponent sidebar;
    private BorderPane root;
    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        boundary = new TranscriptionBoundary(new TranscriptionController());

        HomeBoundary homeBoundary = new HomeBoundary();
        NotesListComponent notesList = new NotesListComponent(homeBoundary, primaryStage, new ArrayList<>(), note -> {});
        sidebar = new SidebarComponent(homeBoundary, primaryStage, homeBoundary.getUserEmail(), homeBoundary.getUserPhotoUrl(), new ArrayList<>(), notesList);
        
        editorComponent = new TranscriptionEditorComponent(boundary);
        controlsComponent = new TranscriptionControlsComponent(boundary, editorComponent, this::showTitlePage, primaryStage);
        summaryComponent = new TranscriptionSummaryComponent();
        titleComponent = new TranscriptionTitleComponent(boundary);

        // Spaziatori per centrare verticalmente
        Region topSpacer = new Region();
        Region bottomSpacer = new Region();
        VBox.setVgrow(topSpacer, Priority.ALWAYS);
        VBox.setVgrow(bottomSpacer, Priority.ALWAYS);

        // VBox per centrare gli elementi
        VBox centerBox = new VBox(10, topSpacer, editorComponent, controlsComponent, bottomSpacer);
        centerBox.setAlignment(Pos.CENTER);

        root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(centerBox);
        root.setStyle("-fx-background-color: #ffffff;");

        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setTitle("Audio Transcriptor");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showTitlePage(Transcription transcription) {
        titleComponent.displayTitleInput(primaryStage, root);
    }    
}
