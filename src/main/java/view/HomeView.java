package view;

import control.AuthController;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class HomeView {

    public void start(Stage primaryStage) {
        // Recupera le informazioni dell'utente loggato
        String email = AuthController.getUserEmail();
        String photoUrl = AuthController.getUserPhotoUrl();
        
        // Pane principale
        BorderPane root = new BorderPane();
        
        // Top: Navbar con l'immagine del profilo a sinistra e il tasto logout a destra
        HBox topBar = new HBox(10);
        topBar.setStyle("-fx-padding: 10; -fx-alignment: center;");

        // Immagine del profilo con cornice arrotondata
        ImageView profileImage = new ImageView(new Image(photoUrl));
        profileImage.setFitWidth(50);
        profileImage.setFitHeight(50);

        // Creazione del cerchio per rendere l'immagine rotonda
        Circle clip = new Circle(30, 30, 30);
        profileImage.setClip(clip);

        // Contenitore per immagine profilo
        StackPane profileContainer = new StackPane(profileImage);
        profileContainer.setStyle("-fx-border-color: #0078d7; -fx-border-width: 2px; -fx-background-radius: 50%; -fx-border-radius: 50%;");


        // Email con stile moderno
        Label emailLabel = new Label(email);
        emailLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Bottone Logout a destra
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> logout(primaryStage));
        logoutButton.setStyle("-fx-font-size: 14px; -fx-padding: 5px 10px; -fx-background-color: #0078d7; -fx-text-fill: white;");
        
        // Allineamento della navbar
        HBox.setHgrow(profileImage, Priority.ALWAYS);
        HBox.setHgrow(logoutButton, Priority.ALWAYS);
        topBar.getChildren().addAll(profileImage, emailLabel, logoutButton);
        root.setTop(topBar);

        // Separator (linea orizzontale)
        Separator separator = new Separator();
        separator.setStyle("-fx-border-color: #dcdcdc; -fx-padding: 10;");
        root.setCenter(separator);

        // Central Pane: 4 Card (2 righe, 2 colonne) con strumenti
        GridPane centerPanel = new GridPane();
        centerPanel.setHgap(20);
        centerPanel.setVgap(20);
        centerPanel.setAlignment(Pos.CENTER);

        // Creazione delle 4 card
        String[] toolNames = {"Trascrizione", "Strumento 2", "Strumento 3", "Strumento 4"};

        for (int i = 0; i < 4; i++) {
            final int index = i;
        
            // Crea il bottone per la card senza l'immagine
            Button toolButton = new Button(toolNames[index]);
            toolButton.getStyleClass().add("card-button");  // Applica il CSS per il bottone della card
            toolButton.setMaxWidth(150);

            int row = index / 2;
            int col = index % 2;
        
            toolButton.setOnAction(e -> openToolView(primaryStage, toolNames[index]));
            centerPanel.add(toolButton, col, row);
        }

        root.setCenter(centerPanel);
        
        // Scene and show
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/view/styles.css").toExternalForm());
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openToolView(Stage primaryStage, String toolName) {
        // Placeholder per aprire la vista del tool (ad esempio Trascrizione)
        if ("Trascrizione".equals(toolName)) {
            openTranscriptionView(primaryStage);
        } else {
            // Puoi aggiungere altre funzionalità per gli altri strumenti
            System.out.println(toolName + " selezionato");
        }
    }

    private void openTranscriptionView(Stage primaryStage) {
        // Placeholder per la schermata della trascrizione
        TranscriptionView transcriptionView = new TranscriptionView();
        transcriptionView.start(primaryStage);
    }

    private void logout(Stage primaryStage) {
        // Rimuove il token e reindirizza alla schermata di login
        AuthController.logout();
        LoginView loginView = new LoginView();
        loginView.start(primaryStage);
    }
}
