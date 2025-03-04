package entity;

import java.util.UUID;

public class Note {
    private String id;
    private String uid;
    private String title;
    private String content;

    public Note() {
    }

    public Note(String id, String uid, String title, String content) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.uid = uid;
        this.title = title != null && !title.isBlank() ? title : "Trascrizione";
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }    

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }    

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }    
}
