package config;

public class AppConfig {
    public enum StorageMode {
        DATABASE, FILE_SYSTEM
    }
    private static StorageMode storageMode = StorageMode.DATABASE;

    public static StorageMode getStorageMode() {
        return storageMode;
    }

    public static void setStorageMode(StorageMode mode) {
        storageMode = mode;
    }
}
