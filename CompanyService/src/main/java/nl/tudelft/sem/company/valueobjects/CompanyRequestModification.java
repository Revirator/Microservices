package nl.tudelft.sem.company.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestModification {

    private Long modificationId;

    private String studentId;

    private String companyId;

    private Long serviceId;

    private int hoursPerWeek;

    private int totalHours;

    private double pricePerHour;

    private boolean acceptedByCompany;

    /**
     * Constructor for testing purposes.
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
