package nl.tudelft.sem.company.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    @Id
    private String username;
    private String name;
    private Double averageRating;

    /**
     * Constructor for company without username.
     * Used for testing purposes only.
     *
     * @param name the name of the company
     * @param averageRating rating between 1.00 and 5.00 (inclusive)
     */
    public Company(String name, Double averageRating) {
        this.name = name;
        this.averageRating = averageRating;
    }
}