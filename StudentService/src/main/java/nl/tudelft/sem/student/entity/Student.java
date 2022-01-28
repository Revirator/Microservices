package nl.tudelft.sem.student.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {

    @Id
    // Do not allow a setter to be made for the netId
    @Setter(AccessLevel.NONE)
    private String netId;
    private String name;
    private int totalHours;
    private boolean available;
    private Double averageRating;

    /**
     * Constructor for a Student, including netID and name, without authentication.
     *
     * @param netId unique ID for each student
     * @param name  the name of the student
     */
    public Student(String netId, String name) {
        this.netId = netId;
        this.name = name;
    }
}
