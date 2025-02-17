package persistence;

import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.Firestore;
import entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.CompletableFuture;

public class FirebaseUsersDAO {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseUsersDAO.class);
    private final Firestore db = FirestoreClient.getFirestore();

    public void saveUser(User user) {
        try {
            logger.info("Saving user with ID: {}", user.getId());
            CompletableFuture.runAsync(() -> {
                try {
                    db.collection("users").document(user.getId()).set(user).get();
                    logger.info("User saved successfully in the 'users' collection.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrupted while saving user", e);
                } catch (Exception e) {
                    throw new IllegalStateException("Error saving user in Firestore: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error while saving user", e);
        }
    }
}
