package view;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView();
        loginView.start(primaryStage); // Avvia direttamente la schermata di login
    }

    public static void main(String[] args) {
        launch(args);
    }
}
