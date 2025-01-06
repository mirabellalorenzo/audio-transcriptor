package control;

import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class GoogleAuthProvider {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String CLIENT_ID = dotenv.get("CLIENT_ID");
    private static final String REDIRECT_URI = "http://localhost";  // URL di reindirizzamento

    public static String getGoogleIdToken() {
        try {
            String authUrl = "https://accounts.google.com/o/oauth2/auth"
                    + "?client_id=" + CLIENT_ID
                    + "&redirect_uri=" + REDIRECT_URI
                    + "&response_type=token"
                    + "&scope=email%20profile%20openid";
    
            System.out.println("🌍 Aprendo il browser per l'autenticazione Google...");
            
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(authUrl));  // Apre il browser
            } else {
                System.err.println("❌ Desktop non supportato. Apri manualmente questo link: " + authUrl);
            }
    
            System.out.println("🔗 Incolla qui il token ricevuto dal browser: ");
            Scanner scanner = new Scanner(System.in);
            String idToken = scanner.nextLine();
            scanner.close();
    
            return idToken;
        } catch (Exception e) {
            System.err.println("❌ Errore nell'autenticazione con Google: " + e.getMessage());
            e.printStackTrace();  // Stampa errore dettagliato
            return null;
        }
    }
    
}
