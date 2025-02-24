package persistence;

import entity.Note;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCNotesDAO implements NotesDAO {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    public JDBCNotesDAO() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver MariaDB non trovato", e);
        }
    }

    @Override
    public void save(Note note) {
        String sql = "INSERT INTO notes (id, uid, title, content) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE uid=?, title=?, content=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, note.getId());
            stmt.setString(2, note.getUid());
            stmt.setString(3, note.getTitle());
            stmt.setString(4, note.getContent());
            stmt.setString(5, note.getUid());
            stmt.setString(6, note.getTitle());
            stmt.setString(7, note.getContent());
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Violazione di vincolo di integrità: l'ID potrebbe essere duplicato.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Errore SQL durante il salvataggio della nota", e);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT id, uid, title, content FROM notes"; // ✅ Evitato SELECT *
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notes.add(new Note(
                        rs.getString("id"),
                        rs.getString("uid"),
                        rs.getString("title"),
                        rs.getString("content")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Errore SQL nel recupero delle note", e);
        }
        return notes;
    }

    @Override
    public Note getById(String id) {
        String sql = "SELECT id, uid, title, content FROM notes WHERE id = ?"; // ✅ Evitato SELECT *
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Note(
                        rs.getString("id"),
                        rs.getString("uid"),
                        rs.getString("title"),
                        rs.getString("content")
                );
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Errore SQL nel recupero della nota con ID: " + id, e);
        }
        return null;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new IllegalArgumentException("Tentativo di eliminare una nota inesistente con ID: " + id, e);
        } catch (SQLException e) {
            throw new IllegalStateException("Errore SQL nella cancellazione della nota con ID: " + id, e);
        }
    }
}
