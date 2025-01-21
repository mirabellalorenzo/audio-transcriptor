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

    public static User getCurrentUser() {
        return currentUser;
    }

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
                    throw new IOException("Errore nella registrazione: codice " + status);
                }
            };

            String responseBody = client.execute(request, responseHandler);
            JSONObject responseObject = new JSONObject(responseBody);

            if (responseObject.has("idToken")) {
                // Effettua il login e crea l'utente
                if (login(email, password)) {
                    FirebaseUsersDAO userDao = new FirebaseUsersDAO();
                    userDao.saveUser(currentUser); // Salva l'utente autenticato
                    return true;
                }
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
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
                    return responseBody;
                } else {
                    throw new IOException("Errore nel login: " + responseBody);
                }
            };

            String responseBody = client.execute(request, responseHandler);
            JSONObject responseObject = new JSONObject(responseBody);

            if (responseObject.has("idToken")) {
                currentUser = new User(
                    responseObject.getString("localId"),
                    responseObject.getString("email"),
                    responseObject.optString("photoUrl", "/images/avatar.png")
                );
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
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

                if (status >= 200 && status < 300) {
                    JSONObject responseObject = new JSONObject(responseBody);

                    if (responseObject.has("idToken")) {
                        currentUser = new User(
                            responseObject.getString("localId"),
                            responseObject.getString("email"),
                            responseObject.optString("photoUrl", "/images/avatar.png")
                        );

                        // Salva l'utente nel database se necessario
                        FirebaseUsersDAO userDao = new FirebaseUsersDAO();
                        userDao.saveUser(currentUser);

                        return true;
                    }
                } else {
                    System.err.println("Errore nel login con Google: codice " + status);
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

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void logout() {
        currentUser = null;
    }
}
