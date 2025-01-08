package boundary;

import control.AuthController;
import control.GoogleAuthProvider;
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
        GoogleAuthProvider.openGoogleLogin();
        new Thread(() -> {
            try {
                while (!AuthController.isLoggedIn()) {
                    Thread.sleep(2000);
                }
                openHomeView(primaryStage);
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
