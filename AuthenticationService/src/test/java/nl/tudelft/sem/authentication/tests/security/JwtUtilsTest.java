package nl.tudelft.sem.authentication.tests.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import nl.tudelft.sem.authentication.security.JwtUtils;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("application-test.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JwtUtilsTest {

    private final JwtUtils jwtUtils = new JwtUtils();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String username = "revirator";
    private final String encodedPassword = encoder.encode("1234");
    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            username,
            encodedPassword,
            new SimpleGrantedAuthority("ROLE_STUDENT"));
    @Mock
    private Authentication authentication;

    /**
     * Used to setup the @value fields in JwtUtils with dummy values.
     */
    @BeforeAll
    public void setupValueFields() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret",
                "ipSbQrMyTmP6IEpH5CvraYrVfZy1eJU7giSj6MgwiZRx86Z9U17rPqyeKVNStDE");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 86400);
    }

    @Test
    public void testTokenGeneration() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);
        assertThat(token)
                // Only works for username "revirator" and password "1234"
                .startsWith("eyJhbGciOiJIUzI1NiJ9."
                        + "eyJzdWIiOiJyZXZpcmF0b3JcblJPTEVfU1RVREVOVCIsImlhdCI6MTY");
    }

    @Test
    public void testGettersAndValidation() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);
        String username = jwtUtils.getUsernameFromJwtToken(token);
        String role = jwtUtils.getRoleFromJwtToken(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertThat(username).isEqualTo("revirator");
        assertThat(role).isEqualTo("ROLE_STUDENT");
    }

    @Test
    public void testInvalidToken() {
        String token = "Random invalid token";
        // Print the trace of an exception but that is
        // because of the try catch in the method
        assertFalse(jwtUtils.validateJwtToken(token));
    }
}
