package nl.tudelft.sem.authentication.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private String username;
    private String password;
    private String role;
    @Transient
    private String name;
}
