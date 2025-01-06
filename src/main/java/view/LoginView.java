package view;

import control.AuthController;
import control.GoogleAuthProvider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private Stage primaryStage; // Manteniamo lo Stage per il cambio scena

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Salviamo il riferimento allo Stage

        Label title = new Label("Login");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Inserisci l'email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci la password");

        Button loginButton = new Button("Accedi");
        Button signUpButton = new Button("Registrati");
        Button googleSignInButton = new Button("Accedi con Google");

        Label statusLabel = new Label();

        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("⚠️ Inserisci email e password!");
                return;
            }
            boolean success = AuthController.login(email, password);
            if (success) {
                statusLabel.setText("✅ Login effettuato con successo!");
                openTranscriptionView(); // Passa alla schermata di trascrizione
            } else {
                statusLabel.setText("❌ Errore nel login");
            }
        });

        signUpButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("⚠️ Inserisci email e password!");
                return;
            }
            boolean success = AuthController.signUp(email, password);
            statusLabel.setText(success ? "✅ Registrazione completata!" : "❌ Errore nella registrazione");
        });

        googleSignInButton.setOnAction(e -> {
            String googleToken = GoogleAuthProvider.getGoogleIdToken(); // Ottiene il token di Google OAuth
            if (googleToken != null) {
                boolean success = AuthController.loginWithGoogle(googleToken);
                statusLabel.setText(success ? "✅ Login con Google effettuato!" : "❌ Errore nel login con Google");
                if (success) {
                    openTranscriptionView();
                }
            } else {
                statusLabel.setText("❌ Errore nell'autenticazione con Google");
            }
        });


        VBox root = new VBox(10, title, emailField, passwordField, loginButton, signUpButton, googleSignInButton, statusLabel);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Login Firebase");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Metodo per passare alla TranscriptionView
    private void openTranscriptionView() {
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
