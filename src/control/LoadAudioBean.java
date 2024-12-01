package control;

public class LoadAudioBean {

    private String filePath;

    public LoadAudioBean(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
