package config;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

public class GoogleAuthProvider {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String CLIENT_SECRET = dotenv.get("CLIENT_SECRET");
    private static final String REDIRECT_URI = "http://localhost:5000/callback";
    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthProvider.class);

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

                logger.info("Opening browser for Google authentication: {}", authUrl);

                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.contains("win")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + authUrl);
                } else if (osName.contains("mac")) {
                    Runtime.getRuntime().exec("open " + authUrl);
                } else {
                    Runtime.getRuntime().exec("xdg-open " + authUrl);
                }
            } catch (IOException e) {
                logger.error("Error opening browser for Google authentication: {}", e.getMessage(), e);
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

            logger.info("Google response received: {}", responseBody);
            return new JSONObject(responseBody).optString("id_token", null);
        } catch (Exception e) {
            logger.error("Error retrieving token from Google: {}", e.getMessage(), e);
            return null;
        }
    }
}
