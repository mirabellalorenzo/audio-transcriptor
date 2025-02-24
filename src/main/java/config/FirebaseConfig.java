package config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
    private static boolean initialized = false;

    private FirebaseConfig() {
        throw new UnsupportedOperationException("FirebaseConfig is a utility class and cannot be instantiated.");
    }

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
            logger.info("Firebase initialized successfully!");

        } catch (IOException e) {
            logger.error("Error initializing Firebase", e);
        }
    }
}
