package persistence;

import entity.Note;
import entity.User;
import control.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileSystemNotesDAO implements NotesDAO {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemNotesDAO.class);
    private final File notesDirectory = new File("notes");

    public FileSystemNotesDAO() {
        if (!notesDirectory.exists()) {
            if (notesDirectory.mkdir()) {
                logger.info("Notes directory created successfully.");
            } else {
                logger.error("Failed to create notes directory.");
            }
        }
    }

    @Override
    public void save(Note note) throws IOException {
        File noteFile = new File(notesDirectory, note.getUid() + "_" + note.getTitle() + ".txt");
        try (FileWriter writer = new FileWriter(noteFile)) {
            writer.write(note.getContent());
            logger.info("Note saved successfully: {}", noteFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Failed to save note: " + note.getTitle(), e); // ✅ No log duplicato
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();

        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            logger.warn("Attempted to retrieve notes, but user is not authenticated.");
            return notes;
        }
        String uid = currentUser.getId();

        if (!notesDirectory.exists() || !notesDirectory.isDirectory()) {
            logger.warn("Notes directory does not exist or is not a directory.");
            return notes;
        }

        for (File file : notesDirectory.listFiles((dir, name) -> name.endsWith(".txt"))) {
            try {
                String[] parts = file.getName().split("_", 2);
                if (parts.length < 2 || !parts[0].equals(uid)) continue; // Filtra per UID

                String title = parts[1].replace(".txt", "");
                String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
                notes.add(new Note(UUID.randomUUID().toString(), uid, title, content));
                logger.info("Loaded note: {}", title);
            } catch (IOException e) {
                logger.error("Error reading note file: {}", file.getName(), e);
            }
        }
        logger.info("Loaded {} notes for user: {}", notes.size(), uid);
        return notes;
    }

    @Override
    public Note getById(String id) {
        logger.warn("Method getById is not implemented.");
        return null;
    }

    @Override
    public void delete(String id) throws IOException {
        File noteFile = new File(notesDirectory, id + ".txt");
        if (noteFile.exists()) {
            try {
                Files.delete(noteFile.toPath());
                logger.info("Note deleted successfully: {}", id);
            } catch (IOException e) {
                throw new IOException("Error deleting note: " + id, e); // ✅ Nessun doppio log
            }
        } else {
            logger.warn("Attempted to delete a note that does not exist: {}", id);
        }
    }
}
