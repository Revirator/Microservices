package nl.tudelft.sem.request.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long serviceId;
    private int hoursPerWeek;
    private int totalHours;
    private double salaryPerHour;

    /**
     * Constructor for the request class without ID for testing purposes.
     *
     * @param hoursPerWeek the number of hours per week
     * @param totalHours the number of hours
     * @param salaryPerHour the salary
     */
    public Request(int hoursPerWeek, int totalHours, double salaryPerHour) {
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.salaryPerHour = salaryPerHour;
    }
}
