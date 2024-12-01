package control;

public class SaveTranscriptionBean {

    private String filePath;

    public SaveTranscriptionBean(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
