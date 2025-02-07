import static spark.Spark.*;

import control.AuthController;
import control.FirebaseConfig;
import control.GoogleAuthProvider;
import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;
import java.util.logging.Logger;


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
    }

    private void startServerOAuth() {
        port(5000); // Porta 5000 per la callback OAuth

        get("/callback", (req, res) -> {
            String code = req.queryParams("code"); // Recupero il codice OAuth
            if (code == null) {
                LOGGER.severe("Errore: Nessun codice ricevuto da Google.");
                return "Errore nell'autenticazione";
            }

            LOGGER.info("Codice ricevuto: " + code);

            // Scambio il codice per un token
            String idToken = GoogleAuthProvider.getIdTokenFromGoogle(code);
            if (idToken == null) {
                LOGGER.severe("Errore nel recupero dell'ID Token.");
                return "Errore nell'autenticazione";
            }

            LOGGER.info("ID Token ricevuto!");
            
            // Effettuo il login con Firebase
            boolean loginSuccess = AuthController.loginWithGoogle(idToken);
            if (loginSuccess) {
                LOGGER.info("Login con Firebase riuscito!");
                return "Login completato! Puoi chiudere questa finestra.";
            } else {
                LOGGER.severe("Errore nel login con Firebase.");
                return "Errore nel login.";
            }
        });

        LOGGER.info("Server OAuth in ascolto su http://localhost:5000/callback");
    }

    public static void main(String[] args) {
        launch(args);
    }
}