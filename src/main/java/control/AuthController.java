package control;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import io.github.cdimascio.dotenv.Dotenv;

public class AuthController {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String FIREBASE_API_KEY = dotenv.get("FIREBASE_API_KEY");

    // Variabili per memorizzare i dettagli dell'utente
    private static String userToken = null;
    private static String userEmail = null;
    private static String userPhotoUrl = null;

    public static boolean signUp(String email, String password) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;
            HttpPost request = new HttpPost(url);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("returnSecureToken", true);

            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));

            HttpClientResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                if (status >= 200 && status < 300) {
                    return new String(response.getEntity().getContent().readAllBytes());
                } else {
                    throw new IOException("❌ Errore nella registrazione: codice " + status);
                }
            };

            String responseBody = client.execute(request, responseHandler);
            JSONObject responseObject = new JSONObject(responseBody);

            if (responseObject.has("idToken")) {
                System.out.println("✅ Registrazione riuscita! Token ricevuto: " + responseObject.getString("idToken"));
                return true;
            } else {
                System.err.println("❌ Errore nella registrazione: " + responseObject.toString());
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ Errore nella registrazione: " + e.getMessage());
            return false;
        }
    }

    public static boolean login(String email, String password) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
            HttpPost request = new HttpPost(url);
    
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("returnSecureToken", true);
    
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));
    
            HttpClientResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                if (status >= 200 && status < 300) {
                    System.out.println("✅ Login riuscito! Risposta Firebase: " + responseBody);
                    return responseBody;
                } else {
                    System.err.println("❌ Errore nel login: codice " + status);
                    System.err.println("❌ Risposta Firebase: " + responseBody);
                    throw new IOException("Errore nel login: " + responseBody);
                }
            };
    
            String responseBody = client.execute(request, responseHandler);
            JSONObject responseObject = new JSONObject(responseBody);
    
            if (responseObject.has("idToken")) {
                String token = responseObject.getString("idToken");
                System.out.println("✅ Token ricevuto: " + token);
    
                // Salva il token e i dettagli dell'utente (email e foto)
                setUserToken(token);
                setUserEmail(responseObject.optString("email", "Email non disponibile"));
    
                // Se photoUrl è vuoto o null, assegna l'immagine di fallback
                String photoUrl = responseObject.optString("photoUrl", null);
                if (photoUrl == null || photoUrl.isEmpty()) {
                    photoUrl = "/images/avatar.png"; // Percorso dell'immagine di fallback
                }
                setUserPhotoUrl(photoUrl); // Usa il percorso dell'immagine di fallback
    
                return true;
            } else {
                System.err.println("❌ Errore nel login: " + responseObject.toString());
                return false;
            }
    
        } catch (Exception e) {
            System.err.println("❌ Eccezione nel login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }    

    public static boolean loginWithGoogle(String idToken) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + FIREBASE_API_KEY;
            HttpPost request = new HttpPost(url);
    
            JSONObject json = new JSONObject();
            json.put("postBody", "id_token=" + idToken + "&providerId=google.com");
            json.put("requestUri", "http://localhost");
            json.put("returnSecureToken", true);
    
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));
    
            HttpClientResponseHandler<Boolean> responseHandler = response -> {
                int status = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
    
                System.out.println("🔹 Risposta Firebase: " + responseBody);
    
                if (status >= 200 && status < 300) {
                    JSONObject responseObject = new JSONObject(responseBody);
    
                    if (responseObject.has("idToken")) {
                        String token = responseObject.getString("idToken");
                        System.out.println("Token ricevuto da Firebase: " + token);
                        
                        // SALVA IL TOKEN E I DETTAGLI
                        setUserToken(token);
                        setUserEmail(responseObject.optString("email", "Email non disponibile"));
                        setUserPhotoUrl(responseObject.optString("photoUrl", "URL foto non disponibile"));
    
                        return true;
                    }
                } else {
                    System.err.println("Errore nel login con Firebase. Codice: " + status);
                    System.err.println("Risposta Firebase: " + responseBody);
                }
                return false;
            };
    
            return client.execute(request, responseHandler);
    
        } catch (Exception e) {
            System.err.println("Errore nel login con Firebase: " + e.getMessage());
            return false;
        }
    }    

    // Metodi per ottenere e impostare i dettagli dell'utente
    private static void setUserToken(String token) {
        userToken = token;
    }

    private static void setUserEmail(String email) {
        userEmail = email;
    }

    private static void setUserPhotoUrl(String photoUrl) {
        userPhotoUrl = photoUrl;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public static boolean isLoggedIn() {
        return userToken != null;
    }
    
    public static String getUserToken() {
        return userToken;
    }

    public static void logout() {
        userToken = null;
        userEmail = null;
        userPhotoUrl = null;
    }
}
