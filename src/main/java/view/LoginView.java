package view;

import control.AuthController;
import control.GoogleAuthProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;


public class LoginView extends Application {
    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Layout per Login
        VBox loginLayout = createLoginLayout();
        loginScene = new Scene(loginLayout, 400, 500);
        loginScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        // Layout per Register
        VBox registerLayout = createRegisterLayout();
        registerScene = new Scene(registerLayout, 400, 500);
        registerScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginLayout() {
        Label title = new Label("Login");
        title.getStyleClass().add("title");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");
    
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("button");
    
        // Logo di Google
        ImageView googleIcon = new ImageView(new Image(getClass().getResource("/images/google.png").toExternalForm()));
        googleIcon.setFitWidth(20);
        googleIcon.setFitHeight(20);

        // Testo del pulsante
        Label googleText = new Label("Continue with Google");

        // Contenitore per icona + testo
        HBox googleContent = new HBox(10, googleIcon, googleText);
        googleContent.setAlignment(Pos.CENTER_LEFT);

        // Pulsante Google
        Button googleSignInButton = new Button();
        googleSignInButton.setGraphic(googleContent);
        googleSignInButton.getStyleClass().add("google-button");
        googleSignInButton.setMinWidth(220);
    
        // Barra con "or" tra i pulsanti
        HBox orBox = new HBox(10);
        Label orLabel = new Label("or");
        orBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        orBox.getChildren().add(orLabel);
    
        Label statusLabel = new Label();
    
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Enter email and password!");
                return;
            }
    
            boolean success = AuthController.login(email, password);
            if (success) {
                statusLabel.setText("✅ Login successful!");
                Platform.runLater(() -> openHomeView());
            } else {
                statusLabel.setText("❌ Error logging in");
            }
        });
    
        googleSignInButton.setOnAction(e -> {
            GoogleAuthProvider.openGoogleLogin();
            new Thread(() -> {
                try {
                    while (!AuthController.isLoggedIn()) {
                        Thread.sleep(2000);
                    }
    
                    Platform.runLater(() -> openHomeView());
    
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
    
        // Link per passare alla registrazione
        Label registerLabel = new Label("Don't have an account? ");
        Hyperlink registerLink = new Hyperlink("Register");
        registerLink.setOnAction(e -> switchToRegisterPage());
    
        HBox registerBox = new HBox(10, registerLabel, registerLink);
        registerBox.setStyle("-fx-alignment: center;");
    
        VBox loginLayout = new VBox(15, title, emailField, passwordField, loginButton, orBox, googleSignInButton, registerBox);
        loginLayout.setStyle("-fx-padding: 30; -fx-alignment: center;");
    
        return loginLayout;
    }    

    private VBox createRegisterLayout() {
        Label title = new Label("Register");
        title.getStyleClass().add("title");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("text-field");
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("password-field");
    
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("button");
    
        Button googleSignInButton = new Button("Continue with Google");
        googleSignInButton.getStyleClass().add("google-button");
    
        Label statusLabel = new Label();
    
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
            if (email.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Enter email and password!");
                return;
            }
    
            boolean success = AuthController.signUp(email, password);
            statusLabel.setText(success ? "✅ Registration complete!" : "❌ Error in registration");
        });
    
        googleSignInButton.setOnAction(e -> {
            GoogleAuthProvider.openGoogleLogin();
            new Thread(() -> {
                try {
                    while (!AuthController.isLoggedIn()) {
                        Thread.sleep(2000);
                    }
    
                    Platform.runLater(() -> openHomeView());
    
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
    
        // Link per tornare al login
        Label loginLabel = new Label("Already have an account? ");
        Hyperlink loginLink = new Hyperlink("Login");
        loginLink.setOnAction(e -> switchToLoginPage());
    
        HBox loginBox = new HBox(10, loginLabel, loginLink);
        loginBox.setStyle("-fx-alignment: center;");
    
        // Barra con "or" tra i pulsanti
        HBox orBox = new HBox(10);
        Label orLabel = new Label("or");
        orBox.setStyle("-fx-alignment: center; -fx-padding: 10;");
        orBox.getChildren().add(orLabel);
    
        VBox registerLayout = new VBox(15, title, emailField, passwordField, registerButton, orBox, googleSignInButton, loginBox);
        registerLayout.setStyle("-fx-padding: 30; -fx-alignment: center;");
    
        return registerLayout;
    }    

    private void switchToRegisterPage() {
        primaryStage.setScene(registerScene);
    }

    private void switchToLoginPage() {
        primaryStage.setScene(loginScene);
    }

    private void openHomeView() {
        HomeView homeView = new HomeView();
        homeView.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
