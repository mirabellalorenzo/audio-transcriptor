package boundary;

import control.AuthController;
import javafx.stage.Stage;
import view.LoginView;
import view.TranscriptionView;

public class HomeBoundary {

    public String getUserEmail() {
        return AuthController.getCurrentUser() != null ? AuthController.getCurrentUser().getEmail() : "Email non disponibile";
    }

    public String getUserPhotoUrl() {
        return AuthController.getCurrentUser() != null ? AuthController.getCurrentUser().getPhotoUrl() : "/images/avatar.png";
    }

    public void logout(Stage primaryStage) {
        AuthController.logout();
        openLoginView(primaryStage);
    }

    public void openToolView(Stage primaryStage, String toolName) {
        System.out.println("openToolView chiamato con: " + toolName);
        if ("Transcribe Audio".equals(toolName)) {
            System.out.println("Apertura di TranscriptionView...");
            TranscriptionView transcriptionView = new TranscriptionView();
            transcriptionView.start(primaryStage);
        } else {
            System.out.println(toolName + " non gestito.");
        }
    }    

    private void openLoginView(Stage primaryStage) {
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }
}
