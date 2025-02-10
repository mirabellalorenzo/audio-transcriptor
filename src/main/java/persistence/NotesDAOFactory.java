package persistence;

import config.AppConfig;

public class NotesDAOFactory {
    public static NotesDAO getNotesDAO() {
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            return new FirebaseNotesDAO();
        } else {
            return new FileSystemNotesDAO();
        }
    }
}
