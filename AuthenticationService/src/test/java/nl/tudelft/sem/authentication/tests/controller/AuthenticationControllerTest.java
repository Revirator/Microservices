package nl.tudelft.sem.authentication.tests.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.authentication.controller.AuthenticationController;
import nl.tudelft.sem.authentication.entity.AuthenticationResponse;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.entity.ValidationResponse;
import nl.tudelft.sem.authentication.repository.UserRepository;
import nl.tudelft.sem.authentication.security.JwtUtils;
import nl.tudelft.sem.authentication.service.AuthenticationService;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private final String userPayload = "{\n"
            + "    \"username\": \"revirator\",\n"
            + "    \"password\": \"1234\",\n"
            + "    \"role\": \"ROLE_STUDENT\",\n"
            + "    \"name\": \"TEST\"\n"
            + "}";
    private final String token = "Random token";
    private final String username = "revirator";
    private final String role = "ROLE_STUDENT";
    private final String password = "1234";
    private final String registerUrl = "/auth/register";
    private final String encoded = "encoded";
    private final String encodedPassword = encoder.encode(password);
    private final UserDetailsImpl userDetails = new UserDetailsImpl(
            username,
            encodedPassword,
            new SimpleGrantedAuthority(role));
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private Authentication authentication;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationService authenticationService;
    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
                authenticationController).build();
    }

    @Test
    public void testLogin() throws Exception {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(token);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        String payload = "{\n"
                + "    \"username\": \"revirator\",\n"
                + "    \"password\": \"1234\"\n"
                + "}";
        String res = mockMvc.perform(post("/auth/login")
                        .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationResponse response = gson.fromJson(res, AuthenticationResponse.class);
        assertEquals(response.getToken(), token);
        assertEquals(response.getType(), "Bearer");
        assertEquals(response.getUsername(), username);
        assertEquals(response.getRole(), role);
        assertEquals(SecurityContextHolder.getContext().getAuthentication(), authentication);
        verify(authenticationManager).authenticate(authToken);
        verify(jwtUtils).generateJwtToken(authentication);
        verify(authentication).getPrincipal();
    }

    @Test
    public void testInvalidTokenValidation() throws Exception {
        when(jwtUtils.validateJwtToken(token)).thenReturn(false);
        String res = mockMvc.perform(post("/auth/validate")
                        .content(token).contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ValidationResponse response = gson.fromJson(res, ValidationResponse.class);
        assertFalse(response.isTokenIsValid());
        assertNull(response.getUsername());
        assertNull(response.getRole());
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils, never()).getUsernameFromJwtToken(token);
        verify(jwtUtils, never()).getRoleFromJwtToken(token);
    }

    @Test
    public void testValidTokenValidation() throws Exception {
        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUsernameFromJwtToken(token)).thenReturn(username);
        when(jwtUtils.getRoleFromJwtToken(token)).thenReturn(role);
        String res = mockMvc.perform(post("/auth/validate")
                        .content(token).contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ValidationResponse response = gson.fromJson(res, ValidationResponse.class);
        assertTrue(response.isTokenIsValid());
        assertEquals(response.getUsername(), username);
        assertEquals(response.getRole(), role);
        verify(jwtUtils).validateJwtToken(token);
        verify(jwtUtils).getUsernameFromJwtToken(token);
        verify(jwtUtils).getRoleFromJwtToken(token);
    }

    @Test
    public void testValidUserRegistration() throws Exception {
        // Mocks for the generateAuthenticationResponse method
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);
        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(token);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock for the register method
        User newUser = gson.fromJson(userPayload, User.class);
        newUser.setPassword(encoded);
        when(passwordEncoder.encode("1234")).thenReturn(encoded);
        given(authenticationService.register(newUser)).willReturn(true);
        String res = mockMvc.perform(post(registerUrl)
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthenticationResponse response = gson.fromJson(res, AuthenticationResponse.class);
        assertEquals(response.getUsername(), username);
        assertEquals(response.getRole(), role);
        verify(authenticationManager).authenticate(authToken);
        verify(jwtUtils).generateJwtToken(authentication);
        verify(authentication).getPrincipal();
        verify(authenticationService).register(newUser);
    }

    @Test
    public void testInvalidUserRegistration() throws Exception {
        User newUser = gson.fromJson(userPayload, User.class);
        newUser.setPassword(encoded);
        when(passwordEncoder.encode("1234")).thenReturn(encoded);
        given(authenticationService.register(newUser)).willReturn(false);
        mockMvc.perform(post(registerUrl)
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(authenticationService).register(newUser);
    }
}
