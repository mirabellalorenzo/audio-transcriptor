package control;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {

    private static boolean initialized = false;

    public static void initializeFirebase() {
        if (initialized) return;
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            System.out.println("Firebase inizializzato con successo!");

        } catch (IOException e) {
            System.err.println("Errore durante l'inizializzazione di Firebase: " + e.getMessage());
        }
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
