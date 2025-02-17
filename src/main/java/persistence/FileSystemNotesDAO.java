package persistence;

import entity.Note;
import entity.User;
import control.AuthController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import io.github.cdimascio.dotenv.Dotenv;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.security.GeneralSecurityException;


public class FileSystemNotesDAO implements NotesDAO {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemNotesDAO.class);
    private final File notesDirectory = new File("notes");
    private static final Dotenv dotenv = Dotenv.load();
    private static final String AES_KEY = dotenv.get("AES_KEY");

    public FileSystemNotesDAO() {
        if (!notesDirectory.exists() && notesDirectory.mkdir()) {
            logger.info("Notes directory created successfully.");
        }
        
        if (AES_KEY == null || AES_KEY.length() != 32) {
            throw new IllegalStateException("AES_KEY deve essere lunga 32 caratteri (256 bit).");
        }
    }

    @Override
    public void save(Note note) throws IOException {
        ensureNoteHasId(note);
        File newNoteFile = getNoteFile(note);
        deleteOldNoteFiles(note, newNoteFile);

        try {
            String encryptedContent = encryptAES(note.getContent(), AES_KEY);
            writeEncryptedFile(newNoteFile, encryptedContent);
            setFilePermissions(newNoteFile);
            logger.info("Nota salvata in formato crittografato: {}", newNoteFile.getAbsolutePath());
        } catch (GeneralSecurityException e) {
            throw new IOException("Errore nella crittografia della nota: " + note.getTitle(), e);
        } catch (IOException e) {
            throw new IOException("Errore di I/O durante il salvataggio della nota: " + note.getTitle(), e);
        }        
    }

    private void ensureNoteHasId(Note note) {
        if (note.getId() == null || note.getId().isBlank()) {
            note.setId(UUID.randomUUID().toString());
        }
    }

    private File getNoteFile(Note note) {
        String safeTitle = note.getTitle().replaceAll("[^a-zA-Z0-9]", "_");
        return new File(notesDirectory, note.getId() + "_" + note.getUid() + "_" + safeTitle + ".txt");
    }

    private void deleteOldNoteFiles(Note note, File newNoteFile) {
        File[] existingFiles = notesDirectory.listFiles(
            (dir, name) -> name.startsWith(note.getId() + "_") && name.endsWith(".txt")
        );
    
        if (existingFiles != null) {
            for (File existingFile : existingFiles) {
                if (!existingFile.getName().equals(newNoteFile.getName()) && existingFile.delete()) {
                    logger.info("Eliminato vecchio file della nota: {}", existingFile.getName());
                }
            }
        }
    }

    private void writeEncryptedFile(File file, String content) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
            outputStream.write(content.getBytes());
        }
    }

    private void setFilePermissions(File file) throws IOException {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (!file.setReadable(false, false)) {
                logger.warn("Impossibile disabilitare la lettura del file: {}", file.getAbsolutePath());
            }
            if (!file.setWritable(true, true)) {
                logger.warn("Impossibile rendere scrivibile il file: {}", file.getAbsolutePath());
            }
            if (!file.setExecutable(false, false)) {
                logger.warn("Impossibile disabilitare l'esecuzione del file: {}", file.getAbsolutePath());
            }
        } else {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rw-------");
            Files.setPosixFilePermissions(file.toPath(), perms);
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
        
                if (parts.length < 3 || !parts[1].equals(currentUid)) {
                    logger.warn("File ignorato: {}", file.getName());
                    continue;
                }
        
                String noteId = parts[0];
                String noteUid = parts[1];
                String titleWithExtension = parts[2];
        
                String rawTitle = titleWithExtension.replaceAll("\\.txt$", "");
                String formattedTitle = rawTitle.replace("_", " ");
        
                String encryptedContent = new String(Files.readAllBytes(file.toPath()));
                String decryptedContent = decryptAES(encryptedContent, AES_KEY);
        
                notes.add(new Note(noteId, noteUid, formattedTitle, decryptedContent));
                logger.info("Nota caricata: {}", formattedTitle);
            } catch (Exception e) {
                logger.error("Errore durante la lettura e la decrittografia del file: {}", file.getName(), e);
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
    
    // Method for encrypting text with AES-256 CBC
    private static String encryptAES(String data, String key) throws GeneralSecurityException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
    
            byte[] iv = new byte[12];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
    
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
    
            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encrypted);
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }    

    // Method for decrypting text with AES-256 GCM
    private static String decryptAES(String encryptedData, String key) throws GeneralSecurityException {
        try {
            String[] parts = encryptedData.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Formato dati non valido");
            }

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] encrypted = Base64.getDecoder().decode(parts[1]);

            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);

        } catch (GeneralSecurityException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new GeneralSecurityException("Errore nel formato dei dati crittografati", e);
        }
    }
}
