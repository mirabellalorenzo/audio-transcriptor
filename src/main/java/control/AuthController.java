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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final Dotenv dotenv = Dotenv.load();
    private static User currentUser;

    private static final String FIREBASE_API_KEY = dotenv.get("FIREBASE_API_KEY");
    private static final String ID_TOKEN_KEY = "idToken";
    private static final String EMAIL_KEY = "email";
    private static final String PASSWORD_KEY = "password";
    private static final String RETURN_SECURE_TOKEN_KEY = "returnSecureToken";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private AuthController() {
        throw new UnsupportedOperationException("Utility class - instantiation not allowed");
    }

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
                    logger.info("Firebase request successful: {}", url);
                    return responseBody;
                } else {
                    logger.error("Firebase request failed: {}, Response: {}", url, responseBody);
                    throw new IOException("Firebase request error: " + responseBody);
                }
            };

            return new JSONObject(client.execute(request, responseHandler));
        }
    }

    public static boolean signUp(String email, String password) {
        try {
            logger.info("User attempting to sign up with email: {}", email);
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + FIREBASE_API_KEY;

            JSONObject json = new JSONObject();
            json.put(EMAIL_KEY, email);
            json.put(PASSWORD_KEY, password);
            json.put(RETURN_SECURE_TOKEN_KEY, true);

            JSONObject responseObject = sendFirebaseRequest(url, json);

            if (responseObject.has(ID_TOKEN_KEY)) {
                logger.info("User registered successfully: {}", email);
                return login(email, password);
            }
        } catch (Exception e) {
            logger.error("Error during registration for email: {} - {}", email, e.getMessage(), e);
        }
        return false;
    }

    public static boolean login(String email, String password) {
        try {
            logger.info("User attempting to log in with email: {}", email);
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

            JSONObject json = new JSONObject();
            json.put(EMAIL_KEY, email);
            json.put(PASSWORD_KEY, password);
            json.put(RETURN_SECURE_TOKEN_KEY, true);

            JSONObject responseObject = sendFirebaseRequest(url, json);

            if (responseObject.has(ID_TOKEN_KEY)) {
                currentUser = new User(
                    responseObject.getString("localId"),
                    responseObject.getString(EMAIL_KEY),
                    responseObject.optString("photoUrl", "/images/avatar.png")
                );
                logger.info("User logged in successfully: {}", email);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error during login for email: {} - {}", email, e.getMessage(), e);
        }
        return false;
    }

    public static boolean loginWithGoogle(String idToken) {
        try {
            logger.info("User attempting to log in with Google.");
            String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=" + FIREBASE_API_KEY;

            JSONObject json = new JSONObject();
            json.put("postBody", "id_token=" + idToken + "&providerId=google.com");
            json.put("requestUri", "http://localhost");
            json.put(RETURN_SECURE_TOKEN_KEY, true);

            JSONObject responseObject = sendFirebaseRequest(url, json);

            if (responseObject.has(ID_TOKEN_KEY)) {
                currentUser = new User(
                    responseObject.getString("localId"),
                    responseObject.getString(EMAIL_KEY),
                    responseObject.optString("photoUrl", "/images/avatar.png")
                );

                FirebaseUsersDAO userDao = new FirebaseUsersDAO();
                userDao.saveUser(currentUser);
                logger.info("User logged in with Google: {}", currentUser.getEmail());
                return true;
            }
        } catch (Exception e) {
            logger.error("Error with Google login - {}", e.getMessage(), e);
        }
        return false;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getEmail());
        }
        currentUser = null;
    }
}
