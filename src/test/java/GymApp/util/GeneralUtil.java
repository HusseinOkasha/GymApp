package GymApp.util;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;


public class GeneralUtil {

    public static String login(String username, String password, String url, RestTemplate restTemplate) {

        // Create the basic auth request.
        String plainCredentials = username + ":" + password;
        byte[] plainCredentialsBytes = plainCredentials.getBytes();

        // Encode the basic authentication request.
        byte[] base64CredentialsBytes = Base64.encodeBase64(plainCredentialsBytes);
        String base64Credentials = new String(base64CredentialsBytes);

        // Create the header object
        HttpHeaders basicAuthHeaders = new HttpHeaders();
        basicAuthHeaders.add("Authorization", "Basic " + base64Credentials);

        // Perform login to get the token.
        HttpEntity<String> basicAuthRequest = new HttpEntity<String>(basicAuthHeaders);
        String token = restTemplate.postForObject(url, basicAuthRequest
                , String.class);

        return token;
    }

    // utility method to get the base url
    public static String getBaseUrl(int port) {
        return "http://localhost:" + port + "/api";
    }
}
