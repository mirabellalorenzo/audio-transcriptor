package entity;

public class User {
    private String id;
    private String email;
    private String photoUrl;

    // Costruttore vuoto richiesto da Firebase
    public User() {}

    public User(String id, String email, String photoUrl) {
        this.id = id;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
