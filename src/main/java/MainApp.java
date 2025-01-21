

import static spark.Spark.*;

import control.AuthController;
import control.FirebaseConfig;
import control.GoogleAuthProvider;
import javafx.application.Application;
import javafx.stage.Stage;
import view.LoginView;

public class MainApp extends Application {
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
                System.err.println("Errore: Nessun codice ricevuto da Google.");
                return "Errore nell'autenticazione";
            }

            System.out.println("Codice ricevuto: " + code);

            // Scambio il codice per un token
            String idToken = GoogleAuthProvider.getIdTokenFromGoogle(code);
            if (idToken == null) {
                System.err.println("Errore nel recupero dell'ID Token.");
                return "Errore nell'autenticazione";
            }

            System.out.println("ID Token ricevuto!");
            
            // Effettuo il login con Firebase
            boolean loginSuccess = AuthController.loginWithGoogle(idToken);
            if (loginSuccess) {
                System.out.println("Login con Firebase riuscito!");
                return "Login completato! Puoi chiudere questa finestra.";
            } else {
                System.err.println("Errore nel login con Firebase.");
                return "Errore nel login.";
            }
        });

        System.out.println("Server OAuth in ascolto su http://localhost:5000/callback");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
