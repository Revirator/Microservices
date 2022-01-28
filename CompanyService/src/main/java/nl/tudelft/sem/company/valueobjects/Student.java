package nl.tudelft.sem.company.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    private String netId;
    private String name;
    private int totalHours;
    //private String[] competencies = new String[0];
    private boolean available;
    private Double averageRating;
}