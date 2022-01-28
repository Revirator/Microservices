package nl.tudelft.sem.authentication.tests.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.gateway.service.AuthenticatedUserService;
import nl.tudelft.sem.gateway.valueobject.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedUserServiceTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AuthenticationResponse authenticationResponse;
    @InjectMocks
    private AuthenticatedUserService authenticatedUserService;

    @Test
    public void testNoBearerToken() {
        AuthenticationResponse response = authenticatedUserService.isAuthenticated("Random token");
        assertFalse(response.isTokenIsValid());
        assertNull(response.getUsername());
        assertNull(response.getRole());
    }

    @Test
    public void testBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZXZpcmF0b3JcblJPTE", headers);
        when(restTemplate
                .postForObject("http://AUTHENTICATION-SERVICE/auth/validate",
                        request,
                        AuthenticationResponse.class))
                .thenReturn(new AuthenticationResponse(true, "revirator", "ROLE_STUDENT"));
        AuthenticationResponse response = authenticatedUserService
                .isAuthenticated("Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyZXZpcmF0b3JcblJPTE");
        assertTrue(response.isTokenIsValid());
        assertTrue(response.getUsername().equals("revirator"));
        assertTrue(response.getRole().equals("ROLE_STUDENT"));
        verify(authenticationResponse).setTokenIsValid(true);
        verify(authenticationResponse).setUsername("revirator");
        verify(authenticationResponse).setRole("ROLE_STUDENT");
    }
}
