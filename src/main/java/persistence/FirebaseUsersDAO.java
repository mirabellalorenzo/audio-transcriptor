package persistence;

import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import entity.User;

public class FirebaseUsersDAO {
    private final Firestore db = FirestoreClient.getFirestore();

    public void saveUser(User user) {
        try {
            db.collection("users").document(user.getId()).set(user).get();
            System.out.println("✅ Utente salvato con successo nella collection 'users'.");
        } catch (Exception e) {
            throw new RuntimeException("❌ Errore durante il salvataggio dell'utente: " + e.getMessage(), e);
        }
    }
}
