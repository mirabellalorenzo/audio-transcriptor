package view.gui2;

import control.HomeController;
import control.NoteBean;
import config.AppConfig;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import view.components.FlatNotesListComponent;
import view.components.NavbarComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class HomeView2 {
    private static final Logger logger = LoggerFactory.getLogger(HomeView2.class);
    private final HomeController homeController;

    public HomeView2(AppConfig appConfig) {
        this.homeController = new HomeController(appConfig);
    }

    public void start(Stage primaryStage) {
        List<NoteBean> notes = homeController.getSavedNotes();

        String userEmail = homeController.getUserEmail();
        String userPhotoUrl = homeController.getUserPhotoUrl();

        NavbarComponent navbar = new NavbarComponent(homeController, primaryStage, userEmail, userPhotoUrl);
        FlatNotesListComponent flatNotesListComponent = new FlatNotesListComponent(homeController, primaryStage, notes, this::openNoteDetail);

        BorderPane content = new BorderPane();
        content.setTop(navbar);
        content.setCenter(flatNotesListComponent);

        StackPane root = new StackPane();
        root.getChildren().add(content);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Notes - Home");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void openNoteDetail(NoteBean note) {
        logger.info("Nota selezionata: {}", note.getTitle());
    }    
}
