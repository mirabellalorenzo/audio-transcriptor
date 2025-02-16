package boundary;

import control.AuthController;
import javafx.application.Platform;
import javafx.stage.Stage;
import util.GoogleAuthProvider;
import view.gui1.HomeView;

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
        GoogleAuthProvider.openGoogleLogin(); // Avvia il processo di login
        new Thread(() -> {
            try {
                while (!AuthController.isLoggedIn()) {
                    Thread.sleep(2000); // Attendi che l'utente completi il login
                }
                Platform.runLater(() -> openPageView(primaryStage)); // Passa alla home nel thread JavaFX
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); // ✅ Ripristina lo stato di interruzione
                logger.error("Login thread interrupted: {}", ex.getMessage(), ex);
            }
        }).start();
    }    

    public void openPageView(Stage primaryStage) {
        HomeBoundary homeBoundary = new HomeBoundary();
        homeBoundary.openPageView(primaryStage, "Notes"); // Ora chiama il boundary
    }    
}
