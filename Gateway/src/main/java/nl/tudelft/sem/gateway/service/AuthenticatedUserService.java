package nl.tudelft.sem.gateway.service;

import nl.tudelft.sem.gateway.valueobject.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticatedUserService {

    @Autowired
    private AuthenticationResponse authenticationResponse;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * Sends a request to the authentication microservice to validate the
     * jwt token from the authorization header.
     *
     * @param jwt the value of the Authorization header
     * @return true if the token is valid plus the username and role of the user
     *      inside a ValueObject class.
     */
    public AuthenticationResponse isAuthenticated(String jwt) {
        if (!jwt.startsWith("Bearer ")) {
            return new AuthenticationResponse(false, null, null);
        }
        // Remove the "Bearer " part
        jwt = jwt.substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(jwt, headers);
        AuthenticationResponse response = restTemplate
                .postForObject("http://AUTHENTICATION-SERVICE/auth/validate", request, AuthenticationResponse.class);
        // Saving the information to the autowired variable so that it can be used by the filter
        this.authenticationResponse.setTokenIsValid(response.isTokenIsValid());
        this.authenticationResponse.setUsername(response.getUsername());
        this.authenticationResponse.setRole(response.getRole());
        return response;
    }
}
