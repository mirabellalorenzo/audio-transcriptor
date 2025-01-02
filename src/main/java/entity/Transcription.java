package entity;

public class Transcription {
    private String text;
    private int duration; // Durata in secondi
    private long createdAt; // Timestamp

    public Transcription(String text, int duration, long createdAt) {
        this.text = text;
        this.duration = duration;
        this.createdAt = createdAt;
    }

    public String getText() {
        return text;
    }

    public int getDuration() {
        return duration;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
