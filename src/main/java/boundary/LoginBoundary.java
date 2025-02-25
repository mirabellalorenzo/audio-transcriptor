package boundary;

import control.AuthController;
import javafx.application.Platform;
import javafx.stage.Stage;
import control.UserBean;
import util.GoogleAuthProvider;

public class LoginBoundary {

    public UserBean login(UserBean userBean, Stage primaryStage) {
        if (userBean == null || userBean.getEmail() == null || userBean.getPassword() == null) {
            throw new IllegalArgumentException("Invalid user data.");
        }

        UserBean user = AuthController.login(userBean.getEmail(), userBean.getPassword());
        if (user == null) {
            throw new IllegalStateException("Login failed.");
        }

        openPageView(primaryStage);
        return user;
    }

    public UserBean register(UserBean userBean, Stage primaryStage) {
        if (userBean == null || userBean.getEmail() == null || userBean.getPassword() == null) {
            throw new IllegalArgumentException("Invalid user data.");
        }

        UserBean user = AuthController.signUp(userBean.getEmail(), userBean.getPassword());
        if (user == null) {
            throw new IllegalStateException("Registration failed.");
        }

        openPageView(primaryStage);
        return user;
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
                throw new IllegalStateException("Login process interrupted.", ex);
            }
        }).start();
    }

    public void openPageView(Stage primaryStage) {
        if (primaryStage == null) {
            throw new IllegalArgumentException("Primary stage cannot be null.");
        }
        HomeBoundary homeBoundary = new HomeBoundary();
        homeBoundary.openPageView(primaryStage, "Notes");
    }
}
