package control;

public class TranscriptionBean {
    private String title;
    private String text;
    private String filePath;
    private final int duration;
    private final long createdAt;
    private final long processingTime;

    public TranscriptionBean(String title, String text, int duration, long createdAt, long processingTime) {
        this.title = title;
        this.text = text;
        this.duration = duration;
        this.createdAt = createdAt;
        this.processingTime = processingTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getDuration() {
        return duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWordCount() {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }

    public int getCharacterCount() {
        if (text == null) {
            return 0;
        }
        return text.replace(" ", "").length();
    }
}