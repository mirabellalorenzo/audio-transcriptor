package control;

public class TranscriptionBean {
    private String text;
    private String filePath;
    private int duration;
    private long createdAt;
    private long processingTime;
    private String title;

    public TranscriptionBean() {}

    public TranscriptionBean(String text, int duration, long createdAt, long processingTime) {
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

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
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