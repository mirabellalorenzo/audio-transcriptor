package persistence;

import entity.Note;
import java.io.IOException;
import java.util.List;

public interface NotesDAO {
    void save(Note note) throws IOException;
    List<Note> getAll() throws IOException;
    Note getById(String id) throws IOException;
    void delete(String id) throws IOException;
}
