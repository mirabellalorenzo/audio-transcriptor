package entity;

import java.util.UUID;

public class Transcription {
    private final String id;
    private final long createdAt;
    private final String text;
    private final int duration;
    private final long processingTime;

    public Transcription(String text, int duration, long createdAt, long processingTime) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.createdAt = createdAt;
        this.duration = duration;
        this.processingTime = processingTime;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getDuration() {
        return duration;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public int getCharacterCount() {
        return text.length();
    }

    public int getWordCount() {
        return text.isEmpty() ? 0 : text.split("\\s+").length;
    }
}
