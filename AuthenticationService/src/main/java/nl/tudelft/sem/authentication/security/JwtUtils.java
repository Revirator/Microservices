package nl.tudelft.sem.authentication.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import nl.tudelft.sem.authentication.service.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Provides utility functions for working with jwt tokens.
 *
 * @Sources: https://www.bezkoder.com/spring-boot-jwt-mysql-spring-security-architecture/
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * Generates a new jwt token signed with the jwtSecret and encoded using HS256.
     *
     * @param authentication contains the user details of an authenticated user
     * @return the newly generated jwt token
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetailsImpl.getUsername()
                        + "\n"
                        + userDetailsImpl.getAuthority().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date().getTime() + jwtExpirationMs)))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    /**
     * Return the username of the user by decoding the jwt token.
     *
     * @param token the token for which we want the username
     * @return the decoded username
     */
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
                .split("\n")[0];
    }

    /**
     * Return the role of the user by decoding the jwt token.
     *
     * @param token the token for which we want the role
     * @return the decoded role
     */
    public String getRoleFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject()
                .split("\n")[1];
    }

    /**
     * Receives a String which is checked whether it is a valid jwt token
     * signed using the jwtSecret.
     *
     * @param token the String which we want to valid
     * @return true if it is a valid jwt token and false otherwise
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
