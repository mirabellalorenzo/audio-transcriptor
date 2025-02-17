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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;


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

        try {
            String encryptedContent = encryptAES(note.getContent(), AES_KEY);
            Files.write(newNoteFile.toPath(), encryptedContent.getBytes(), StandardOpenOption.CREATE);
            logger.info("Nota salvata in formato crittografato: {}", newNoteFile.getAbsolutePath());
        } catch (Exception e) {
            throw new IOException("Errore durante la crittografia e il salvataggio della nota: " + note.getTitle(), e);
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
    
    // Metodo per crittografare il testo con AES-256 CBC
    private static String encryptAES(String data, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        byte[] encrypted = cipher.doFinal(data.getBytes());

        return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(encrypted);
    }

    // Metodo per decrittografare il testo con AES-256 CBC
    private static String decryptAES(String encryptedData, String key) throws Exception {
        String[] parts = encryptedData.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Formato dati non valido");

        byte[] iv = Base64.getDecoder().decode(parts[0]);
        byte[] encrypted = Base64.getDecoder().decode(parts[1]);

        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
}
