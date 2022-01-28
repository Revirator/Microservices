package nl.tudelft.sem.gateway.valueobject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private boolean tokenIsValid;
    private String username;
    private String role;
}
