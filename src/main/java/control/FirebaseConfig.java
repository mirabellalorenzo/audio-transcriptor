package control;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private static boolean initialized = false;

    public static void initializeFirebase() {
        if (initialized) return;
        try {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/serviceAccountKey.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://audio-transcriptor-24e9e.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;
            logger.info("Firebase inizializzato con successo!"); // INFO invece di System.out.println

        } catch (IOException e) {
            logger.error("Errore durante l'inizializzazione di Firebase", e); // ERROR invece di System.err.println
        }
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
