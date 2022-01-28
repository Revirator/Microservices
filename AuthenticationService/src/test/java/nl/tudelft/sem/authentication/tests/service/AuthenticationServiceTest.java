package nl.tudelft.sem.authentication.tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Optional;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.repository.UserRepository;
import nl.tudelft.sem.authentication.service.AuthenticationService;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import nl.tudelft.sem.authentication.valueobjects.RequestUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String username = "revirator";
    private final String encoded = "encoded";
    private String role = "ROLE_STUDENT";
    private User user = new User(username, encoder.encode("1234"), role, "TEST");
    private final Gson gson = new GsonBuilder().create();
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testUserFoundInDatabase() {
        given(userRepository.findById(username)).willReturn(Optional.of(user));
        UserDetails test = authenticationService.loadUserByUsername(username);
        assertEquals(test, UserDetailsImpl.build(user));
    }

    @Test
    public void testUserNotFoundInDatabase() {
        given(userRepository.findById(username)).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () ->
                authenticationService.loadUserByUsername(username));
    }

    @Test
    public void testUsernameInUseAlready() {
        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        assertFalse(authenticationService.register(user));
        verify(userRepository).findById(username);
    }

    @Test
    public void testInvalidRole() {
        when(userRepository.findById(username)).thenReturn(Optional.empty());
        user.setRole(role.replace("ROLE_", "role_"));
        assertFalse(authenticationService.register(user));
        user.setRole(role.replace("STUDENT", "test"));
        assertFalse(authenticationService.register(user));
        user.setRole(role);
        verify(userRepository, times(2)).findById(username);
    }

    @Test
    public void testValidStudentRegistration() {
        User newUser = new User(username, encoded, role, "TEST");
        when(userRepository.findById(username)).thenReturn(Optional.empty());
        when(userRepository.save(newUser)).thenReturn(newUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userJsonObject = gson
                .toJson(new RequestUser(newUser.getUsername(), newUser.getName()));
        HttpEntity<String> request = new HttpEntity<>(userJsonObject, headers);
        when(restTemplate.postForObject("http://STUDENT-SERVICE/student/"
                + username + "/", request, Object.class)).thenReturn(new Object());
        assertTrue(authenticationService.register(newUser));
        verify(userRepository).findById(username);
        verify(userRepository).save(newUser);
        verify(restTemplate).postForObject("http://STUDENT-SERVICE/student/" + username + "/", request, Object.class);
    }

    @Test
    public void testValidCompanyRegistration() {
        User newUser = new User(username, encoded, role.replace("STUDENT", "COMPANY"), "TEST");
        when(userRepository.findById(username)).thenReturn(Optional.empty());
        when(userRepository.save(newUser)).thenReturn(newUser);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String userJsonObject = gson
                .toJson(new RequestUser(newUser.getUsername(), newUser.getName()));
        HttpEntity<String> request = new HttpEntity<>(userJsonObject, headers);
        when(restTemplate.postForObject("http://COMPANY-SERVICE/company/",
                request, Object.class)).thenReturn(new Object());
        assertTrue(authenticationService.register(newUser));
        verify(userRepository).findById(username);
        verify(userRepository).save(newUser);
        verify(restTemplate).postForObject("http://COMPANY-SERVICE/company/", request, Object.class);
    }
}
