package persistence;

import entity.Note;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryNotesDAO implements NotesDAO {
    private final ConcurrentHashMap<String, Note> notes = new ConcurrentHashMap<>();

    @Override
    public void save(Note note) {
        if (note.getId() == null || note.getId().isBlank()) {
            note.setId(java.util.UUID.randomUUID().toString());
        }
        notes.put(note.getId(), note);
    }

    @Override
    public List<Note> getAll() {
        return new ArrayList<>(notes.values());
    }

    @Override
    public Note getById(String id) {
        return notes.get(id);
    }

    @Override
    public void delete(String id) {
        notes.remove(id);
    }
}
