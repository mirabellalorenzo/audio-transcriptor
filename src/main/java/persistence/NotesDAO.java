package persistence;

import entity.Note;
import java.io.IOException;
import java.util.List;

public interface NotesDAO {
    void save(Note note) throws IOException; // Metodo per salvare una nota
    List<Note> getAll() throws IOException; // Metodo per ottenere tutte le note
    Note getById(String id) throws IOException; // Metodo per ottenere una nota per ID
    void delete(String id) throws IOException; // Metodo per eliminare una nota per ID
}
