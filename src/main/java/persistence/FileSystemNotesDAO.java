package persistence;

import entity.Note;
import entity.User;
import control.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
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
        if (note.getId() == null || note.getId().isBlank()) {
            note.setId(UUID.randomUUID().toString());
        }

        String safeTitle = note.getTitle().replaceAll("[^a-zA-Z0-9]", "_");

        File newNoteFile = new File(notesDirectory, note.getId() + "_" + note.getUid() + "_" + safeTitle + ".txt");

        File[] existingFiles = notesDirectory.listFiles((dir, name) -> name.startsWith(note.getId() + "_") && name.endsWith(".txt"));
        
        if (existingFiles != null) {
            for (File existingFile : existingFiles) {
                if (!existingFile.getName().equals(newNoteFile.getName())) {
                    if (existingFile.delete()) {
                        logger.info("Eliminato vecchio file della nota: {}", existingFile.getName());
                    } else {
                        logger.warn("Impossibile eliminare il vecchio file della nota: {}", existingFile.getName());
                    }
                }
            }
        }

        try (FileWriter writer = new FileWriter(newNoteFile)) {
            writer.write(note.getContent());
            logger.info("Nota salvata correttamente: {}", newNoteFile.getAbsolutePath());
        } catch (IOException e) {
            throw new IOException("Impossibile salvare la nota: " + note.getTitle(), e);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
    
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            logger.warn("Recupero note non effettuato: utente non autenticato.");
            return notes;
        }
        String currentUid = currentUser.getId();
    
        if (!notesDirectory.exists() || !notesDirectory.isDirectory()) {
            logger.warn("La directory delle note non esiste o non è una directory.");
            return notes;
        }
    
        for (File file : notesDirectory.listFiles((dir, name) -> name.endsWith(".txt"))) {
            try {
                String[] parts = file.getName().split("_", 3);
                if (parts.length < 3) continue;
    
                String noteId = parts[0];
                String noteUid = parts[1];
                String titleWithExtension = parts[2];
                if (!noteUid.equals(currentUid)) continue;
    
                String rawTitle = titleWithExtension.replaceAll("\\.txt$", "");
                String formattedTitle = rawTitle.replace("_", " ");
    
                String content = new String(Files.readAllBytes(file.toPath()));
                notes.add(new Note(noteId, noteUid, formattedTitle, content));
                logger.info("Nota caricata: {}", formattedTitle);
            } catch (IOException e) {
                logger.error("Errore durante la lettura del file: {}", file.getName(), e);
            }
        }
        logger.info("Caricate {} note per l'utente: {}", notes.size(), currentUid);
        return notes;
    }    

    @Override
    public Note getById(String id) {
        logger.warn("Method getById is not implemented.");
        return null;
    }

    @Override
    public void delete(String id) throws IOException {
        File[] matchingFiles = notesDirectory.listFiles((dir, name) -> name.startsWith(id + "_") && name.endsWith(".txt"));
        if (matchingFiles != null && matchingFiles.length > 0) {
            for (File file : matchingFiles) {
                try {
                    Files.delete(file.toPath());
                    logger.info("Nota eliminata correttamente: {}", file.getName());
                } catch (IOException e) {
                    throw new IOException("Errore durante l'eliminazione della nota: " + id, e);
                }
            }
        } else {
            logger.warn("Tentativo di eliminare una nota inesistente: {}", id);
        }
    }    
}
