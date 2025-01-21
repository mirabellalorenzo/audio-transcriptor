package persistence;

import entity.Note;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.util.ArrayList;
import java.util.List;

public class FirebaseNotesDAO implements NotesDAO {
    private final Firestore db = FirestoreClient.getFirestore();

    @Override
    public void save(Note note) {
        try {
            db.collection("notes").document(note.getId()).set(note).get();
        } catch (Exception e) {
            throw new RuntimeException("Error saving note", e);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        try {
            for (DocumentSnapshot doc : db.collection("notes").get().get().getDocuments()) {
                notes.add(doc.toObject(Note.class));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching notes", e);
        }
        return notes;
    }

    @Override
    public Note getById(String id) {
        try {
            DocumentSnapshot doc = db.collection("notes").document(id).get().get();
            return doc.toObject(Note.class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching note by ID", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            db.collection("notes").document(id).delete().get();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting note", e);
        }
    }
}
