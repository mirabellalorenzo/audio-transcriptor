package util;

import persistence.NotesDAO;
import persistence.FirebaseNotesDAO;
import persistence.FileSystemNotesDAO;

public class NotesDAOFactory {
    public static NotesDAO getDAO() {
        if (AppConfig.getStorageMode() == AppConfig.StorageMode.DATABASE) {
            return new FirebaseNotesDAO();
        } else {
            return new FileSystemNotesDAO();
        }
    }
}
