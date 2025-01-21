package persistence;

import entity.Note;
import entity.User;
import control.AuthController;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileSystemNotesDAO implements NotesDAO {
    private final File notesDirectory = new File("notes");

    public FileSystemNotesDAO() {
        // Creazione della directory se non esiste
        if (!notesDirectory.exists()) {
            notesDirectory.mkdir();
        }
    }

    @Override
    public void save(Note note) throws IOException {
        File noteFile = new File(notesDirectory, note.getUid() + "_" + note.getTitle() + ".txt");
        try (FileWriter writer = new FileWriter(noteFile)) {
            writer.write(note.getContent());
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();

        // Recupera l'utente corrente
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Errore: utente non autenticato.");
            return notes;
        }
        String uid = currentUser.getId();

        if (!notesDirectory.exists() || !notesDirectory.isDirectory()) return notes;

        for (File file : notesDirectory.listFiles((dir, name) -> name.endsWith(".txt"))) {
            try {
                String[] parts = file.getName().split("_", 2);
                if (parts.length < 2 || !parts[0].equals(uid)) continue; // Filtra per UID

                String title = parts[1].replace(".txt", "");
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                notes.add(new Note(UUID.randomUUID().toString(), uid, title, content));
            } catch (IOException e) {
                System.err.println("Errore durante la lettura della nota: " + e.getMessage());
            }
        }
        return notes;
    }

    @Override
    public Note getById(String id) {
        // Per il file system, non c'è un concetto diretto di "ID",
        // quindi possiamo restituire null o gestire diversamente (ad esempio, basandoci sul titolo).
        return null;
    }

    @Override
    public void delete(String id) throws IOException {
        // Per il file system, supponiamo che `id` sia il titolo.
        File noteFile = new File(notesDirectory, id + ".txt");
        if (noteFile.exists()) {
            Files.delete(noteFile.toPath());
        }
    }
}
