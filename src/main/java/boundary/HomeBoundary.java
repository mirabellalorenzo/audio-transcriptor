package boundary;

import control.AuthController;
import javafx.stage.Stage;
import view.LoginView;
import view.TranscriptionView;

public class HomeBoundary {

    public String getUserEmail() {
        return AuthController.getUserEmail();
    }

    public String getUserPhotoUrl() {
        return AuthController.getUserPhotoUrl();
    }

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }

    public void openToolView(Stage primaryStage, String toolName) {
        if ("Transcribe Audio".equals(toolName)) {
            openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " selected");
        }
    }

    private void openLoginView(Stage primaryStage) {
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }

    private void openTranscriptionView(Stage primaryStage) {
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }
}
