package boundary;

import control.AuthController;
import javafx.application.Platform;
import javafx.stage.Stage;
import util.GoogleAuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginBoundary {
    private static final Logger logger = LoggerFactory.getLogger(LoginBoundary.class);

    public boolean login(String email, String password, Stage primaryStage) {
        boolean success = AuthController.login(email, password);
        if (success) {
            openPageView(primaryStage);
        }
        return success;
    }

    public boolean register(String email, String password, Stage primaryStage) {
        boolean success = AuthController.signUp(email, password);
        if (success) {
            openPageView(primaryStage);
        }
        return success;
    }

    public void loginWithGoogle(Stage primaryStage) {
        GoogleAuthProvider.openGoogleLogin();
        new Thread(() -> {
            try {
                while (!AuthController.isLoggedIn()) {
                    Thread.sleep(2000);
                }
                Platform.runLater(() -> openPageView(primaryStage));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                logger.error("Login thread interrupted: {}", ex.getMessage(), ex);
            }
        }).start();
    }    

    public void openPageView(Stage primaryStage) {
        HomeBoundary homeBoundary = new HomeBoundary();
        homeBoundary.openPageView(primaryStage, "Notes");
    }    
}
