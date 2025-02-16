package view.gui1;

import boundary.LoginBoundary;
import config.AppConfig;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import view.components.CustomButtonComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginView extends Application {
    private Stage primaryStage;
    private Scene loginScene;
    private Scene registerScene;
    private static final String LOGIN_KEY = "Login";
    private static final String REGISTER_KEY = "Register";
    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        LoginBoundary boundary = new LoginBoundary();

        // Layout per Login e Register
        VBox loginLayout = createLoginLayout(boundary);
        StackPane root = new StackPane(loginLayout); // StackPane permette di sovrapporre elementi
        loginScene = new Scene(root, 400, 500);

        VBox registerLayout = createRegisterLayout(boundary);
        registerScene = new Scene(registerLayout, 400, 500);

        primaryStage.setTitle(LOGIN_KEY);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginLayout(LoginBoundary boundary) {
        // **Logo dell'App e Nome in HBox**
        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(10, 15, 20, 15)); // Aggiunge spazio sotto il logo

        // Carica il logo PNG
        ImageView appLogo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        appLogo.setFitWidth(40);
        appLogo.setFitHeight(40);
        appLogo.setPreserveRatio(true);

        Label appNameLabel = new Label("AudioTranscriptor");
        appNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222;");

        logoContainer.getChildren().addAll(appLogo, appNameLabel);


        Label title = new Label(LOGIN_KEY);
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #222;");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(280);
        emailField.setStyle(
            "-fx-background-color: white; " +
            "-fx-font-size: 14px; " +
            "-fx-border-color: #dcdcdc; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12px; " +
            "-fx-min-width: 200px;"
        );
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);
        passwordField.setStyle(emailField.getStyle());
    
        Label errorLabel = new Label();
        errorLabel.setVisible(false);
    
        CustomButtonComponent loginButton = new CustomButtonComponent(LOGIN_KEY, CustomButtonComponent.ButtonType.PRIMARY);
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
    
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Email and password are required!");
                errorLabel.setVisible(true);
                return;
            }
    
            if (boundary.login(email, password, primaryStage)) {
                logger.info("User logged in successfully.");
                errorLabel.setVisible(false);
            } else {
                logger.warn("Login failed: Incorrect email or password.");
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
        registerLink.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        HBox registerBox = new HBox(registerLabel, registerLink);
        registerBox.setAlignment(Pos.CENTER);
    
        Label modeLabel = new Label("Storage Mode:");
        modeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    
        ComboBox<AppConfig.StorageMode> modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll(AppConfig.StorageMode.values());
        modeSelector.setValue(AppConfig.getStorageMode());
        modeSelector.setOnAction(e -> AppConfig.setStorageMode(modeSelector.getValue()));
        modeSelector.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: white; " +
            "-fx-border-color: #dcdcdc; " +
            "-fx-border-radius: 5px; " +
            "-fx-padding: 3px;"
        );

        CustomButtonComponent settingsButton = new CustomButtonComponent("Impostazioni", "settings-outline", CustomButtonComponent.ButtonType.SECONDARY);
        settingsButton.setOnAction(e -> showSettingsModal());
    
        HBox modeBox = new HBox(10, settingsButton);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setStyle("-fx-padding: 20px 0px 0px 0px;");
    
        // **BOX PER IL FORM** (stile simile a TranscriptionTitleComponent)
        VBox formBox = new VBox(15, title, emailField, passwordField, errorLabel, loginButton, orBox, googleSignInButton, registerBox);
        formBox.setStyle(
            "-fx-background-color: #F5F5F5; " +
            "-fx-padding: 25px; " +
            "-fx-background-radius: 30px; " +
            "-fx-background-insets: 0; " +
            "-fx-alignment: center;"
        );
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);
    
        // **WRAPPER CENTRALE**
        VBox wrapper = new VBox(logoContainer, formBox, modeBox);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: white; -fx-padding: 20px;");
        
        return wrapper;
    }    
    
    private VBox createRegisterLayout(LoginBoundary boundary) {
        // **Logo dell'App e Nome in HBox**
        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.setPadding(new Insets(10, 15, 20, 15)); // Aggiunge spazio sotto il logo

        // Carica il logo PNG
        ImageView appLogo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        appLogo.setFitWidth(40);
        appLogo.setFitHeight(40);
        appLogo.setPreserveRatio(true);

        Label appNameLabel = new Label("AudioTranscriptor");
        appNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #222;");

        logoContainer.getChildren().addAll(appLogo, appNameLabel);


        Label title = new Label(REGISTER_KEY);
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #222;");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(280);
        emailField.setStyle(
            "-fx-background-color: white; " +
            "-fx-font-size: 14px; " +
            "-fx-border-color: #dcdcdc; " +
            "-fx-border-radius: 10px; " +
            "-fx-background-radius: 10px; " +
            "-fx-padding: 12px; " +
            "-fx-min-width: 200px;"
        );
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);
        passwordField.setStyle(emailField.getStyle());
    
        Label errorLabel = new Label();
        errorLabel.setVisible(false);
    
        CustomButtonComponent registerButton = new CustomButtonComponent(REGISTER_KEY, CustomButtonComponent.ButtonType.PRIMARY);
        registerButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();
    
            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Email and password are required!");
                errorLabel.setVisible(true);
                return;
            }
    
            if (boundary.register(email, password, primaryStage)) {
                logger.info("User registered successfully.");
                errorLabel.setVisible(false);
            } else {
                logger.warn("Registration failed: Email already in use.");
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
        loginLink.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        HBox loginBox = new HBox(loginLabel, loginLink);
        loginBox.setAlignment(Pos.CENTER);
    
        VBox formBox = new VBox(15, title, emailField, passwordField, errorLabel, registerButton, orBox, googleSignInButton, loginBox);
        formBox.setStyle(
            "-fx-background-color: #F5F5F5; " +
            "-fx-padding: 25px; " +
            "-fx-background-radius: 30px; " +
            "-fx-background-insets: 0; " +
            "-fx-alignment: center;"
        );
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);
    
        VBox wrapper = new VBox(logoContainer, formBox);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setStyle("-fx-background-color: white; -fx-padding: 20px;");
        
        return wrapper;
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
        googleSignInButton.setStyle(
            "-fx-background-color: #F5F5F5;" +
            "-fx-border-color: #ccc;" +
            "-fx-padding: 10;" + 
            "-fx-border-radius: 10px;" +
            "-fx-cursor-pointer: hand;"
        );
        googleSignInButton.setCursor(Cursor.HAND);
        googleSignInButton.setOnAction(e -> boundary.loginWithGoogle(primaryStage));
        return googleSignInButton;
    }

    private void switchToRegisterPage() {
        primaryStage.setScene(registerScene);
    }

    private void switchToLoginPage() {
        primaryStage.setScene(loginScene);
    }

    private void showSettingsModal() {
        VBox modalBox = new VBox(25);
        modalBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-padding: 30px; " +
            "-fx-border-radius: 20px; " +  // ✅ Aggiunto border-radius
            "-fx-background-radius: 20px; " + // ✅ Per JavaFX, serve anche questa proprietà
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);"
        );
        modalBox.setAlignment(Pos.CENTER);
        modalBox.setMaxWidth(380); // Maggiore larghezza
    
        Label title = new Label("Impostazioni");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // **GridPane per migliorare allineamento**
        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(15);
        
        // **Selettore Storage Mode**
        Label storageModeLabel = new Label("Storage Mode:");
        storageModeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        GridPane.setConstraints(storageModeLabel, 0, 0);
    
        ComboBox<AppConfig.StorageMode> storageSelector = new ComboBox<>();
        storageSelector.getItems().addAll(AppConfig.StorageMode.values());
        storageSelector.setValue(AppConfig.getStorageMode());
        storageSelector.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #ccc; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +  // ✅ Anche qui per sicurezza
            "-fx-padding: 5px;"
        );
        storageSelector.setCursor(Cursor.HAND);
        GridPane.setConstraints(storageSelector, 1, 0);
    
        // **Selettore GUI Mode**
        Label guiModeLabel = new Label("GUI Mode:");
        guiModeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        GridPane.setConstraints(guiModeLabel, 0, 1);
    
        ComboBox<AppConfig.GuiMode> guiSelector = new ComboBox<>();
        guiSelector.getItems().addAll(AppConfig.GuiMode.values());
        guiSelector.setValue(AppConfig.getGuiMode());
        guiSelector.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: #f8f9fa; " +
            "-fx-border-color: #ccc; " +
            "-fx-border-radius: 8px; " +
            "-fx-background-radius: 8px; " +  // ✅ Per sicurezza
            "-fx-padding: 5px;"
        );
        guiSelector.setCursor(Cursor.HAND);
        GridPane.setConstraints(guiSelector, 1, 1);
    
        settingsGrid.getChildren().addAll(storageModeLabel, storageSelector, guiModeLabel, guiSelector);
    
        // **Pulsante Salva**
        CustomButtonComponent saveButton = new CustomButtonComponent("Salva", CustomButtonComponent.ButtonType.PRIMARY);
        saveButton.setOnAction(e -> {
            AppConfig.setStorageMode(storageSelector.getValue());
            AppConfig.setGuiMode(guiSelector.getValue());
            hideSettingsModal();
        });
    
        modalBox.getChildren().addAll(title, settingsGrid, saveButton);
    
        // **Overlay sfocato con migliorato border-radius**
        VBox overlay = new VBox();
        overlay.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.5); "
        );
        overlay.setAlignment(Pos.CENTER);
        overlay.getChildren().add(modalBox);
    
        // **Aggiunge il modal al root dello StackPane**
        ((StackPane) loginScene.getRoot()).getChildren().add(overlay);
    
        // **Chiude il modal cliccando fuori**
        overlay.setOnMouseClicked(e -> hideSettingsModal());
    }    
    
    private void hideSettingsModal() {
        StackPane root = (StackPane) loginScene.getRoot();
        root.getChildren().remove(root.getChildren().size() - 1); // Rimuove il modal
    }    

    public static void main(String[] args) {
        launch(args);
    }
}
