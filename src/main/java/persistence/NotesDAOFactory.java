package persistence;

import config.AppConfig;

public class NotesDAOFactory {

    private NotesDAOFactory() {
        throw new UnsupportedOperationException("Utility class - instantiation not allowed");
    }

    public static NotesDAO getNotesDAO() {
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            return new FirebaseNotesDAO();
        } else {
            return new FileSystemNotesDAO();
        }
    }
}
