package config;

public class AppConfig {
    public enum StorageMode {
        DEMO, DATABASE, FILE_SYSTEM
    }

    public enum GuiMode {
        GUI_1, GUI_2
    }

    private static StorageMode storageMode = StorageMode.DATABASE; // Default: Database
    private static GuiMode guiMode = GuiMode.GUI_1; // Default: GUI 1

    public static StorageMode getStorageMode() {
        return storageMode;
    }

    public static void setStorageMode(StorageMode mode) {
        storageMode = mode;
    }

    public static GuiMode getGuiMode() {
        return guiMode;
    }

    public static void setGuiMode(GuiMode mode) {
        guiMode = mode;
    }
}
