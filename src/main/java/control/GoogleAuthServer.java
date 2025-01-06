package control;

import static spark.Spark.*;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GoogleAuthServer {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:5000/callback"; // URI di reindirizzamento

    public static void main(String[] args) {
        port(5000); // Avvia il server sulla porta 5000

        get("/callback", (req, res) -> {
            String code = req.queryParams("code"); // Ottiene il codice di autorizzazione
            if (code != null) {
                System.out.println("✅ Codice di autorizzazione ricevuto: " + code);
        
                // 🔄 Recupero ID Token...
                String idToken = getIdTokenFromGoogle(code);
                
                if (idToken != null) {
                    System.out.println("✅ ID Token ricevuto: " + idToken);
                    boolean success = AuthController.loginWithGoogle(idToken);
                    if (success) {
                        System.out.println("✅ Login con Google e Firebase riuscito!");
                        res.body("Login riuscito! Ora puoi chiudere questa finestra.");
                    } else {
                        System.err.println("❌ Errore nel login con Firebase.");
                        res.body("Errore nel login.");
                    }
                } else {
                    System.err.println("❌ Errore nel recupero dell'ID token.");
                    res.body("Errore nel recupero dell'ID token.");
                }
            } else {
                System.err.println("❌ Nessun codice di autorizzazione ricevuto.");
                res.body("Errore nell'autenticazione.");
            }
            return res.body();
        });        

        System.out.println("🌍 Server avviato su http://localhost:5000/callback");
    }

    /**
     * Scambia il codice OAuth ricevuto da Google per un ID Token.
     */
    private static String getIdTokenFromGoogle(String code) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/token");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
    
            String data = "code=" + code +
                          "&client_id=" + CLIENT_ID +
                          "&client_secret=" + CLIENT_SECRET +
                          "&redirect_uri=" + REDIRECT_URI +
                          "&grant_type=authorization_code";
    
            try (OutputStream os = conn.getOutputStream()) {
                os.write(data.getBytes(StandardCharsets.UTF_8));
            }
    
            Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8);
            String responseBody = scanner.useDelimiter("\\A").next();
            scanner.close();
    
            System.out.println("🔹 Risposta completa da Google: " + responseBody); // 🔥 Stampa la risposta completa
    
            JSONObject json = new JSONObject(responseBody);
            return json.optString("id_token", null);  // 🔥 Restituisce l'ID Token se presente
    
        } catch (Exception e) {
            System.err.println("❌ Errore nel recupero del token da Google: " + e.getMessage());
            return null;
        }
    }    
}
