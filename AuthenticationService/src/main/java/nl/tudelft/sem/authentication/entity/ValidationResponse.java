package nl.tudelft.sem.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationResponse {

    private boolean tokenIsValid;
    private String username;
    private String role;
}
