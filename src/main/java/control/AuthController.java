package control;

import entity.User;
import persistence.FirebaseUsersDAO;

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

    private static User currentUser;
    private static final String ID_TOKEN_KEY = "idToken";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String RETURN_SECURE_TOKEN_KEY = "returnSecureToken";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";


    public static User getCurrentUser() {
        return currentUser;
    }

    private static JSONObject sendFirebaseRequest(String url, JSONObject payload) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader(CONTENT_TYPE, APPLICATION_JSON);
            request.setEntity(new StringEntity(payload.toString(), StandardCharsets.UTF_8));
    
            HttpClientResponseHandler<String> responseHandler = response -> {
                int status = response.getCode();
                String responseBody = new String(response.getEntity().getContent().readAllBytes());
    
                if (status >= 200 && status < 300) {
                    return responseBody;
                } else {
                    throw new IOException("Errore nella richiesta Firebase: " + responseBody);
                }
            };
    
            return new JSONObject(client.execute(request, responseHandler));
        }
    }    

    public static boolean signUp(String email, String password) {
        try {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;
            
            JSONObject json = new JSONObject();
            json.put(EMAIL_KEY, email);
            json.put(PASSWORD_KEY, password);
            json.put(RETURN_SECURE_TOKEN_KEY, true);
    
            JSONObject responseObject = sendFirebaseRequest(url, json);
    
            if (responseObject.has(ID_TOKEN_KEY)) {
                return login(email, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    

    public static boolean login(String email, String password) {
        try {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;
            
            JSONObject json = new JSONObject();
            json.put(EMAIL_KEY, email);
            json.put(PASSWORD_KEY, password);
            json.put(RETURN_SECURE_TOKEN_KEY, true);
    
            JSONObject responseObject = sendFirebaseRequest(url, json);
    
            if (responseObject.has("idToken")) {
                currentUser = new User(
                    responseObject.getString("localId"),
                    responseObject.getString("email"),
                    responseObject.optString("photoUrl", "/images/avatar.png")
                );
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }    

    public static boolean loginWithGoogle(String idToken) {
        try {
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + FIREBASE_API_KEY;
    
            JSONObject json = new JSONObject();
            json.put("postBody", ID_TOKEN_KEY + "=" + idToken + "&providerId=google.com");
            json.put("requestUri", "http://localhost");
            json.put(RETURN_SECURE_TOKEN_KEY, true);
    
            JSONObject responseObject = sendFirebaseRequest(url, json);
    
            if (responseObject.has(ID_TOKEN_KEY)) {
                currentUser = new User(
                    responseObject.getString("localId"),
                    responseObject.getString("email"),
                    responseObject.optString("photoUrl", "/images/avatar.png")
                );
    
                FirebaseUsersDAO userDao = new FirebaseUsersDAO();
                userDao.saveUser(currentUser);
    
                return true;
            }
        } catch (Exception e) {
            System.err.println("Errore nel login con Firebase: " + e.getMessage());
        }
        return false;
    }    

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
