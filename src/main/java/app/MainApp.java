package app;
import static spark.Spark.*;

import control.AuthController;
import javafx.application.Application;
import javafx.stage.Stage;
import util.GoogleAuthProvider;
import view.LoginView;
import java.util.logging.Logger;

import config.FirebaseConfig;


public class MainApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(MainApp.class.getName());

    @Override
    public void start(Stage primaryStage) {
        // Inizializza Firebase
        FirebaseConfig.initializeFirebase();

        // Avvio il mini-server OAuth
        startServerOAuth();

        // Avvio la schermata di login
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
        primaryStage.setMaximized(true);
    }

    private void startServerOAuth() {
        port(5000); // Porta 5000 per la callback OAuth

        get("/callback", (req, res) -> {
            String code = req.queryParams("code"); // Recupero il codice OAuth
            if (code == null) {
                LOGGER.severe("Error: No code received from Google.");
                return "Authentication error.";
            }

            LOGGER.info("Code received: " + code);

            // Scambio il codice per un token
            String idToken = GoogleAuthProvider.getIdTokenFromGoogle(code);
            if (idToken == null) {
                LOGGER.severe("Error retrieving ID Token.");
                return "Authentication error.";
            }

            LOGGER.info("ID Token received!");
            
            // Effettuo il login con Firebase
            boolean loginSuccess = AuthController.loginWithGoogle(idToken);
            if (loginSuccess) {
                LOGGER.info("Login with Firebase successful!");
                return "Login completed! You can close this window.";
            } else {
                LOGGER.severe("Error logging in with Firebase.");
                return "Login error.";
            }
        });

        LOGGER.info("OAuth server listening at http://localhost:5000/callback");
    }

    public static void main(String[] args) {
        launch(args);
    }
}