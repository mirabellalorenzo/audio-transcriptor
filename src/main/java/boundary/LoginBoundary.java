package boundary;

import control.AuthController;
import control.GoogleAuthProvider;

import javafx.application.Platform;
import javafx.stage.Stage;
import view.HomeView;

public class LoginBoundary {

    public boolean login(String email, String password, Stage primaryStage) {
        boolean success = AuthController.login(email, password);
        if (success) {
            openHomeView(primaryStage);
        }
        return success;
    }

    public boolean register(String email, String password, Stage primaryStage) {
        boolean success = AuthController.signUp(email, password);
        if (success) {
            openHomeView(primaryStage);
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
                Platform.runLater(() -> openHomeView(primaryStage)); // Passa alla home nel thread JavaFX
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void openHomeView(Stage primaryStage) {
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }
}
