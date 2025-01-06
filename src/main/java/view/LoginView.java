package view;

import control.AuthController;
import control.GoogleAuthProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private Stage primaryStage; // Stage principale

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

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

        // Login with email and password
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Inserisci email e password!");
                return;
            }
            boolean success = AuthController.login(email, password);
            if (success) {
                statusLabel.setText("✅ Login effettuato con successo!");
                openTranscriptionView();
            } else {
                statusLabel.setText("❌ Errore nel login");
            }
        });

        // Register user
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

        // Login with Google
        googleSignInButton.setOnAction(e -> {
            GoogleAuthProvider.openGoogleLogin(); // Apri il browser per il login

            new Thread(() -> {
                try {
                    while (!AuthController.isLoggedIn()) {
                        System.out.println("🔹 Attesa login...");
                        Thread.sleep(2000);
                    }

                    // Go to transcription page
                    Platform.runLater(() -> {
                        System.out.println("✅ Login completato! Token: " + AuthController.getUserToken());
                        openTranscriptionView();
                    });

                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        VBox root = new VBox(10, title, emailField, passwordField, loginButton, signUpButton, googleSignInButton, statusLabel);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 400, 300);
        primaryStage.setTitle("Login Firebase");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openTranscriptionView() {
        Platform.runLater(() -> {
            TranscriptionView transcriptionView = new TranscriptionView();
            transcriptionView.start(primaryStage);
        });
    }
}
