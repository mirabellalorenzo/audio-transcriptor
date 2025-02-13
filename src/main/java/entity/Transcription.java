package entity;

public class Transcription {
    private String id;
    private String text;
    private int duration;
    private long createdAt;
    private long processingTime;

    public Transcription(String text, int duration, long createdAt, long processingTime) {
        this.id = String.valueOf(System.nanoTime()); 
        this.text = text;
        this.duration = duration;
        this.createdAt = createdAt;
        this.processingTime = processingTime;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public int getCharacterCount() {
        return text.length();
    }

    public int getWordCount() {
        return text.isEmpty() ? 0 : text.split("\\s+").length;
    }
}
