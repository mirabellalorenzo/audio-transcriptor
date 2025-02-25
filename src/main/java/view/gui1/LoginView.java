package view.gui1;

import boundary.LoginBoundary;
import control.UserBean;
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
    private final AppConfig appConfig = new AppConfig();
    private static final String LOGIN_KEY = "Login";
    private static final String REGISTER_KEY = "Register";
    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        LoginBoundary boundary = new LoginBoundary(appConfig);

        // Layout per Login e Register
        VBox loginLayout = createLoginLayout(boundary);
        StackPane root = new StackPane(loginLayout);
        loginScene = new Scene(root, 400, 500);
        loginScene.getStylesheets().add(getClass().getResource("/styles/loginView.css").toExternalForm());

        VBox registerLayout = createRegisterLayout(boundary);
        registerScene = new Scene(registerLayout, 400, 500);
        registerScene.getStylesheets().add(getClass().getResource("/styles/loginView.css").toExternalForm());
        primaryStage.setTitle(LOGIN_KEY);
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private VBox createLoginLayout(LoginBoundary boundary) {
        // **Logo dell'App e Nome in HBox**
        HBox logoContainer = new HBox(10);
        logoContainer.setAlignment(Pos.CENTER);
        logoContainer.getStyleClass().add("logo-container");

        // Carica il logo PNG
        ImageView appLogo = new ImageView(new Image(getClass().getResource("/images/logo.png").toExternalForm()));
        appLogo.setFitWidth(40);
        appLogo.setFitHeight(40);
        appLogo.setPreserveRatio(true);

        Label appNameLabel = new Label("AudioTranscriptor");
        appNameLabel.getStyleClass().add("app-name-label");

        logoContainer.getChildren().addAll(appLogo, appNameLabel);


        Label title = new Label(LOGIN_KEY);
        title.getStyleClass().add("label-title");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(280);
        emailField.getStyleClass().add("text-field");
    
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);
        passwordField.getStyleClass().add("password-field");
    
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

            UserBean userBean = new UserBean();
            userBean.setEmail(email);
            userBean.setPassword(password);

            UserBean loggedInUser = boundary.login(userBean, primaryStage);
            if (loggedInUser != null) {
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
        registerLink.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        HBox registerBox = new HBox(registerLabel, registerLink);
        registerBox.setAlignment(Pos.CENTER);

        CustomButtonComponent settingsButton = new CustomButtonComponent("Impostazioni", "settings-outline", CustomButtonComponent.ButtonType.SECONDARY);
        settingsButton.setOnAction(e -> showSettingsModal());
    
        HBox modeBox = new HBox(10, settingsButton);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.getStyleClass().add("button-settings");
    
        // **BOX PER IL FORM** (stile simile a TranscriptionTitleComponent)
        VBox formBox = new VBox(15, title, emailField, passwordField, errorLabel, loginButton, orBox, googleSignInButton, registerBox);
        formBox.getStyleClass().add("form-box");
        formBox.setMaxWidth(400);
        formBox.setAlignment(Pos.CENTER);
    
        // **WRAPPER CENTRALE**
        VBox wrapper = new VBox(logoContainer, formBox, modeBox);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.getStyleClass().add("root");
        
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
        appNameLabel.getStyleClass().add("app-name-label");

        logoContainer.getChildren().addAll(appLogo, appNameLabel);


        Label title = new Label(REGISTER_KEY);
        title.getStyleClass().add("label-title");
    
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(280);
        emailField.getStyleClass().add("text-field");
    
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

            UserBean userBean = new UserBean();
            userBean.setEmail(email);
            userBean.setPassword(password);

            UserBean registeredUser = boundary.register(userBean, primaryStage);
            if (registeredUser != null) {
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
        loginLink.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        HBox loginBox = new HBox(loginLabel, loginLink);
        loginBox.setAlignment(Pos.CENTER);
    
        VBox formBox = new VBox(15, title, emailField, passwordField, errorLabel, registerButton, orBox, googleSignInButton, loginBox);
        formBox.getStyleClass().add("form-box");
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
        googleSignInButton.getStyleClass().add("button-google");
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
        modalBox.getStyleClass().add("modal-box");
        modalBox.setAlignment(Pos.CENTER);
        modalBox.setMaxWidth(380);

        Label title = new Label("Impostazioni");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane settingsGrid = new GridPane();
        settingsGrid.setHgap(10);
        settingsGrid.setVgap(15);

        Label storageModeLabel = new Label("Storage Mode:");
        storageModeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        GridPane.setConstraints(storageModeLabel, 0, 0);

        ComboBox<AppConfig.StorageMode> storageSelector = new ComboBox<>();
        storageSelector.getItems().addAll(AppConfig.StorageMode.values());
        storageSelector.setValue(appConfig.getStorageMode()); // Usa l'istanza
        storageSelector.getStyleClass().add("selector-box");
        storageSelector.setCursor(Cursor.HAND);
        GridPane.setConstraints(storageSelector, 1, 0);

        Label guiModeLabel = new Label("GUI Mode:");
        guiModeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        GridPane.setConstraints(guiModeLabel, 0, 1);

        ComboBox<AppConfig.GuiMode> guiSelector = new ComboBox<>();
        guiSelector.getItems().addAll(AppConfig.GuiMode.values());
        guiSelector.setValue(appConfig.getGuiMode()); // Usa l'istanza
        guiSelector.getStyleClass().add("selector-box");
        guiSelector.setCursor(Cursor.HAND);
        GridPane.setConstraints(guiSelector, 1, 1);

        settingsGrid.getChildren().addAll(storageModeLabel, storageSelector, guiModeLabel, guiSelector);

        // Salvataggio delle impostazioni
        CustomButtonComponent saveButton = new CustomButtonComponent("Salva", CustomButtonComponent.ButtonType.PRIMARY);
        saveButton.setOnAction(e -> {
            appConfig.setStorageMode(storageSelector.getValue()); // Usa l'istanza
            appConfig.setGuiMode(guiSelector.getValue()); // Usa l'istanza
            logger.info("Storage Mode selezionato: " + appConfig.getStorageMode());
            hideSettingsModal();
        });

        modalBox.getChildren().addAll(title, settingsGrid, saveButton);

        VBox overlay = new VBox();
        overlay.getStyleClass().add("overlay");
        overlay.setAlignment(Pos.CENTER);
        overlay.getChildren().add(modalBox);

        ((StackPane) loginScene.getRoot()).getChildren().add(overlay);

        overlay.setOnMouseClicked(e -> hideSettingsModal());
    }

    private void hideSettingsModal() {
        StackPane root = (StackPane) loginScene.getRoot();
        root.getChildren().remove(root.getChildren().size() - 1);
    }    

    public static void main(String[] args) {
        launch(args);
    }
}
