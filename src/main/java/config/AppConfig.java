package config;

public class AppConfig {
    public enum StorageMode {
        DEMO, DATABASE, FILE_SYSTEM
    }

    public enum GuiMode {
        GUI_1, GUI_2
    }

    private StorageMode storageMode;
    private GuiMode guiMode;

    public AppConfig() {
        this.storageMode = StorageMode.DATABASE;
        this.guiMode = GuiMode.GUI_1;
    }

    public StorageMode getStorageMode() {
        return storageMode;
    }

    public void setStorageMode(StorageMode mode) {
        this.storageMode = mode;
    }

    public GuiMode getGuiMode() {
        return guiMode;
    }

    public void setGuiMode(GuiMode mode) {
        this.guiMode = mode;
    }
}