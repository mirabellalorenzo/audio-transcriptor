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
            Class.forName("org.mariadb.jdbc.Driver"); // Carica il driver JDBC
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Errore nel caricamento del driver MariaDB", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Errore durante il salvataggio della nota", e);
        }
    }

    @Override
    public List<Note> getAll() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes";
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
            throw new RuntimeException("Errore nel recupero delle note", e);
        }
        return notes;
    }

    @Override
    public Note getById(String id) {
        String sql = "SELECT * FROM notes WHERE id = ?";
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
            throw new RuntimeException("Errore nel recupero della nota", e);
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
        } catch (SQLException e) {
            throw new RuntimeException("Errore nella cancellazione della nota", e);
        }
    }
}
