package view;

import boundary.LoginBoundary;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class LoginView extends Application {
    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;
    private static final String LOGIN_KEY = "Login";
    private static final String REGISTER_KEY = "Register";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        LoginBoundary boundary = new LoginBoundary();

        // Layout per Login e Register
        VBox loginLayout = createLoginLayout(boundary);
        loginScene = new Scene(loginLayout, 400, 500);
        loginScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        VBox registerLayout = createRegisterLayout(boundary);
        registerScene = new Scene(registerLayout, 400, 500);
        registerScene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());

        primaryStage.setTitle(LOGIN_KEY);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginLayout(LoginBoundary boundary) {
        Label title = new Label(LOGIN_KEY);
        title.getStyleClass().add("title");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Label per gli errori
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        Button loginButton = new Button(LOGIN_KEY);
        loginButton.getStyleClass().add("button");
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Email and password are required!");
                errorLabel.setVisible(true);
                return;
            }

            if (boundary.login(email, password, primaryStage)) {
                System.out.println("Login successful!");
                errorLabel.setVisible(false);
            } else {
                errorLabel.setText("Incorrect email or password!");
                errorLabel.setVisible(true);
            }
        });

        Button googleSignInButton = createGoogleSignInButton(boundary);

        Label orLabel = new Label("or");
        Separator separatorLeft = new Separator();
        Separator separatorRight = new Separator();
        HBox orBox = new HBox(separatorLeft, orLabel, separatorRight);
        orBox.setAlignment(Pos.CENTER);
        orBox.setSpacing(10);

        Label registerLabel = new Label("Don't have an account?");
        Hyperlink registerLink = new Hyperlink(REGISTER_KEY);
        registerLink.setOnAction(e -> switchToRegisterPage());
        HBox registerBox = new HBox(registerLabel, registerLink);
        registerBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, title, emailField, passwordField, errorLabel, loginButton, orBox, googleSignInButton, registerBox);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");
        return layout;
    }
    
    private VBox createRegisterLayout(LoginBoundary boundary) {
        Label title = new Label(REGISTER_KEY);
        title.getStyleClass().add("title");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
    
        // Label per gli errori
        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
    
        Button registerButton = new Button(REGISTER_KEY);
        registerButton.getStyleClass().add("button");
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
    
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Email and password are required!");
                errorLabel.setVisible(true);
                return;
            }
    
            if (boundary.register(email, password, primaryStage)) {
                System.out.println("✅ Registration successful!");
                errorLabel.setVisible(false);
            } else {
                errorLabel.setText("Registration failed! Email already in use.");
                errorLabel.setVisible(true);
            }
        });
    
        Button googleSignInButton = createGoogleSignInButton(boundary);
    
        Label orLabel = new Label("or");
        Separator separatorLeft = new Separator();
        Separator separatorRight = new Separator();
        HBox orBox = new HBox(separatorLeft, orLabel, separatorRight);
        orBox.setAlignment(Pos.CENTER);
        orBox.setSpacing(10);
    
        Label loginLabel = new Label("Already have an account?");
        Hyperlink loginLink = new Hyperlink(LOGIN_KEY);
        loginLink.setOnAction(e -> switchToLoginPage());
        HBox loginBox = new HBox(loginLabel, loginLink);
        loginBox.setAlignment(Pos.CENTER);
    
        VBox layout = new VBox(15, title, emailField, passwordField, errorLabel, registerButton, orBox, googleSignInButton, loginBox);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");
        return layout;
    }    

    private Button createGoogleSignInButton(LoginBoundary boundary) {
        ImageView googleIcon = new ImageView(new Image(getClass().getResource("/images/google.png").toExternalForm()));
        googleIcon.setFitWidth(20);
        googleIcon.setFitHeight(20);

        Label googleText = new Label("Continue with Google");
        HBox googleContent = new HBox(10, googleIcon, googleText);
        googleContent.setAlignment(Pos.CENTER_LEFT);

        Button googleSignInButton = new Button();
        googleSignInButton.setGraphic(googleContent);
        googleSignInButton.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-padding: 10;");
        googleSignInButton.setOnAction(e -> boundary.loginWithGoogle(primaryStage));
        return googleSignInButton;
    }

    private void switchToRegisterPage() {
        primaryStage.setScene(registerScene);
    }

    private void switchToLoginPage() {
        primaryStage.setScene(loginScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
