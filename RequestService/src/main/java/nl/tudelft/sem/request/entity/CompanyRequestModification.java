package nl.tudelft.sem.request.entity;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestModification {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "user_sequence")
    private Long modificationId;

    private String studentId;

    private String companyId;

    private Long serviceId;

    private int hoursPerWeek;

    private int totalHours;

    private double pricePerHour;

    private boolean acceptedByCompany;

    /**
     * Constructor for student.
     *
     * @param hoursPerWeek the hours per week
     * @param totalHours the total hours
     * @param pricePerHour the price per hour
     */
    public CompanyRequestModification(int hoursPerWeek, int totalHours, double pricePerHour) {
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
    }
}
