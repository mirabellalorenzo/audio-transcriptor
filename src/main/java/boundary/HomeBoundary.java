package boundary;

import control.HomeController;
import control.TranscriptionController;
import javafx.stage.Stage;

public class HomeBoundary {
    private HomeController homeController = new HomeController();

    public String getUserEmail() {
        return homeController.getUserEmail();
    }

    public String getUserPhotoUrl() {
        return homeController.getUserPhotoUrl();
    }

    public void logout(Stage primaryStage) {
        homeController.logout(primaryStage);
    }

    public void openToolView(Stage primaryStage, String toolName) {
        System.out.println("openToolView chiamato con: " + toolName);
        if ("Transcribe Audio".equals(toolName)) {
            TranscriptionBoundary transcriptionBoundary = new TranscriptionBoundary(new TranscriptionController());
            transcriptionBoundary.openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " non gestito.");
        }
    }
}
