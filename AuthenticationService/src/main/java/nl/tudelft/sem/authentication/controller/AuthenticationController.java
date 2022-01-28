package nl.tudelft.sem.authentication.controller;

import javax.validation.Valid;
import nl.tudelft.sem.authentication.entity.AuthenticationRequest;
import nl.tudelft.sem.authentication.entity.AuthenticationResponse;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.entity.ValidationResponse;
import nl.tudelft.sem.authentication.security.JwtUtils;
import nl.tudelft.sem.authentication.service.AuthenticationService;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private PasswordEncoder encoder;

    /**
     * Checks whether the login credentials match a user in the database
     * and generates a new jwt token.
     *
     * @param authenticationRequest contains the login credentials (username and password).
     * @return the token, the type of the token, the username and the role.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse response = generateAuthenticationResponse(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * Generate an authentication response object from the
     * given user credentials given that they are valid.
     * If the username or password combination is incorrect
     * response with status 401 is returned.
     *
     * @param username the username of the user
     * @param password the unencrypted password of the user
     * @return an authentication response with the jwt token if the credentials were valid
     */
    private AuthenticationResponse generateAuthenticationResponse(
            String username, String password) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String role = userDetails.getAuthorities().toArray()[0].toString();
        return new AuthenticationResponse(jwt, userDetails.getUsername(), role);
    }

    /**
     * Receives a jwt token and returns true, the username and the role of the user
     * if it is a valid token.
     * Returns false, null and null otherwise.
     *
     * @param token the jwt token to be validated
     * @return true or false plus the username and role of the user wrapped in a ValueObject class.
     */
    @PostMapping("/validate")
    public ValidationResponse validate(@RequestBody String token) {
        if (!jwtUtils.validateJwtToken(token)) {
            return new ValidationResponse(false, null, null);
        }
        String username = jwtUtils.getUsernameFromJwtToken(token);
        String role = jwtUtils.getRoleFromJwtToken(token);
        return new ValidationResponse(true, username, role);
    }

    /**
     * Receives user credentials to be registered int the system.
     * If the username is already used or the role of the new user is invalid
     * a response with status 400 is returned.
     *
     * @param user the new user to be registered into the system
     * @return an authentication response with the jwt token if the registration was valid
     */
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User user) {
        String oldPassword = user.getPassword();
        String encodedPassword = encoder.encode(oldPassword);
        user.setPassword(encodedPassword);
        if (!authenticationService.register(user)) {
            return ResponseEntity.badRequest().build();
        }
        AuthenticationResponse response = generateAuthenticationResponse(
                user.getUsername(), oldPassword);
        return ResponseEntity.ok(response);
    }
}
