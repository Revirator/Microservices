package nl.tudelft.sem.student.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company {

    private String username;
    private String name;
    private Double averageRating;
}
