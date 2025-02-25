package boundary;

import control.HomeController;
import control.NoteBean;
import config.AppConfig;
import javafx.stage.Stage;
import java.util.List;

public class HomeBoundary {
    private final HomeController homeController;

    public HomeBoundary(AppConfig appConfig) {
        if (appConfig == null) {
            throw new IllegalArgumentException("AppConfig cannot be null");
        }
        this.homeController = new HomeController(appConfig);
    }

    public String getUserEmail() {
        return homeController.getUserEmail();
    }

    public String getUserPhotoUrl() {
        return homeController.getUserPhotoUrl();
    }

    public List<NoteBean> getSavedNotes() {
        return homeController.getSavedNotes();
    }

    public NoteBean createNewNote() {
        return homeController.createNewNote();
    }

    public void updateNote(NoteBean noteBean) {
        homeController.updateNote(noteBean);
    }

    public void deleteNote(NoteBean noteBean) {
        homeController.deleteNote(noteBean);
    }

    public void logout(Stage primaryStage) {
        homeController.logout(primaryStage);
    }

    public void openPageView(Stage primaryStage, String pageName) {
        homeController.openPageView(primaryStage, pageName);
    }     
}
