package nl.tudelft.sem.contract.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//Used to tell JPA that this is a POJO (Plain Old Java Object), represents the data that we want
// to store in a database.
@Entity
//All Lombok annotations below to create constructors, getters and setters for us.
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //The contract ID, which is auto-generated by JPA for us.
    private Long contractId;
    //The hours per week the student is going to work (For example, since its part time, the
    // student is going to work 4 hours per week).
    private int hoursPerWeek;
    //The total hours the company requires the student to work (For example, a 40 hour contract
    // with 4 hours per week would mean the
    //is going to work for 10 weeks).
    private int totalHours;
    //The hourly fee of the student (For example, 12.5 Euros per hour)
    private double pricePerHour;
    //The ID of the student working for the company.
    private String studentId;
    //The ID of the company using the freelance service
    private String companyId;
    //The boolean to see if the contract is terminated
    private boolean terminated;

    /**
     * Constructor to create an object without an id.
     *
     * @param hoursPerWeek hours per weer
     * @param totalHours total number of hours
     * @param pricePerHour the price per hour
     * @param studentId the id of the student
     * @param companyId the id of the company
     * @param terminated if the contract is terminated
     */
    public Contract(int hoursPerWeek, int totalHours, double pricePerHour, String studentId,
                    String companyId, boolean terminated) {
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.studentId = studentId;
        this.companyId = companyId;
        this.terminated = terminated;
    }
}
