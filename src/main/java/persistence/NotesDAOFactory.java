package persistence;

import config.AppConfig;

public class NotesDAOFactory {

    private NotesDAOFactory() {
        throw new UnsupportedOperationException("Utility class - instantiation not allowed");
    }

    public static NotesDAO getNotesDAO(AppConfig appConfig) {
        if (appConfig == null) {
            throw new IllegalArgumentException("AppConfig cannot be null");
        }

        switch (appConfig.getStorageMode()) {
            case DEMO:
                return new InMemoryNotesDAO();
            case DATABASE:
                return new JDBCNotesDAO();
            case FILE_SYSTEM:
                return new FileSystemNotesDAO();
            default:
                throw new IllegalStateException("Storage mode non valido: " + appConfig.getStorageMode());
        }
    }
}
