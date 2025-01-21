package entity;

import java.util.UUID;

public class Note {
    private String id;
    private String uid; // Identificativo dell'utente
    private String title;
    private String content;

    // Costruttore principale
    public Note(String id, String uid, String title, String content) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.uid = uid;
        this.title = title != null && !title.isBlank() ? title : "Trascrizione";
        this.content = content;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Note{" +
               "id='" + id + '\'' +
               ", uid='" + uid + '\'' +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               '}';
    }
}
