package nl.tudelft.sem.authentication.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserDetailsImplTest {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String username = "revirator";
    private final String encodedPassword = encoder.encode("1234");
    private final User user = new User(username, encodedPassword, "ROLE_STUDENT", "TEST");
    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            username,
            encodedPassword,
            new SimpleGrantedAuthority("ROLE_STUDENT"));

    @Test
    public void testBuildUser() {
        UserDetailsImpl test = UserDetailsImpl.build(user);
        assertEquals(test, userDetails);
        assertNotNull(new UserDetailsImpl());
    }

    @Test
    public void testGetters() {
        assertThat(userDetails.getAuthorities())
                .hasSameElementsAs((Iterable) List.of(new SimpleGrantedAuthority("ROLE_STUDENT")));
        assertEquals(userDetails.getUsername(), username);
        assertEquals(userDetails.getPassword(), encodedPassword);
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());
    }
}
