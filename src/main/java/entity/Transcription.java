package entity;

public class Transcription {
    private final String id;
    private final long createdAt;
    private String text;
    private final int duration;
    private final long processingTime;

    public Transcription(String text, int duration, long createdAt, long processingTime) {
        this.createdAt = createdAt;
        this.id = String.valueOf(System.nanoTime());
        this.text = text;
        this.duration = duration;
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
