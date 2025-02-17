package persistence;

import entity.Note;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FirebaseNotesDAO implements NotesDAO {
    private final Firestore db = FirestoreClient.getFirestore();
    private static final String NOTES_KEY = "notes";

    @Override
    public void save(Note note) {
        try {
            db.collection(NOTES_KEY).document(note.getId()).set(note).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while saving note", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error saving note", e);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        try {
            for (DocumentSnapshot doc : db.collection(NOTES_KEY).get().get().getDocuments()) {
                notes.add(doc.toObject(Note.class));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while fetching notes", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error fetching notes", e);
        }
        return notes;
    }

    @Override
    public Note getById(String id) {
        try {
            DocumentSnapshot doc = db.collection(NOTES_KEY).document(id).get().get();
            return doc.toObject(Note.class);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while fetching note by ID", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error fetching note by ID", e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            db.collection(NOTES_KEY).document(id).delete().get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrupted while deleting note", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Error deleting note", e);
        }
    }
}
