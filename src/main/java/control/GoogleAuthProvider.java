package control;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;

public class GoogleAuthProvider {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:5000/callback"; // Callback locale

    public static void openGoogleLogin() {
        new Thread(() -> {
            try {
                String authUrl = "https://accounts.google.com/o/oauth2/auth"
                        + "?client_id=" + CLIENT_ID
                        + "&redirect_uri=" + REDIRECT_URI
                        + "&response_type=code"
                        + "&scope=email%20profile%20openid"
                        + "&access_type=offline"
                        + "&prompt=consent";

                System.out.println("🌍 Aprendo il browser per l'autenticazione Google: " + authUrl);

                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + authUrl);
                } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    Runtime.getRuntime().exec("open " + authUrl);
                } else {
                    Runtime.getRuntime().exec("xdg-open " + authUrl);
                }
            } catch (IOException e) {
                System.err.println("❌ Errore nell'aprire il browser: " + e.getMessage());
            }
        }).start();
    }

    public static String getIdTokenFromGoogle(String code) {
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

            System.out.println("🔹 Risposta Google: " + responseBody);
            return new org.json.JSONObject(responseBody).optString("id_token", null);
        } catch (Exception e) {
            System.err.println("❌ Errore nel recupero del token da Google: " + e.getMessage());
            return null;
        }
    }
}
