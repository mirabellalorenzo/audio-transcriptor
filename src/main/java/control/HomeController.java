package control;

import javafx.stage.Stage;
import view.LoginView;
import view.TranscriptionView;

public class HomeController {

    public static void openToolView(Stage primaryStage, String toolName) {
        if ("Transcribe Audio".equals(toolName)) {
            openTranscriptionView(primaryStage);
        } else {
            System.out.println(toolName + " selected");
        }
    }

    private static void openTranscriptionView(Stage primaryStage) {
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public static void logout(Stage primaryStage) {
        AuthController.logout();
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }
}
