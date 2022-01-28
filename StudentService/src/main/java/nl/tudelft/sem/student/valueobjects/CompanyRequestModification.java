package nl.tudelft.sem.student.valueobjects;

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

    /**
     * Constructor for testing purposes.
     *
     * @param studentId the ID of the student
     * @param serviceId the ID of the service
     * @param hoursPerWeek the hours per week
     * @param totalHours the total hours
     * @param pricePerHour the price per hour
     * @param acceptedByCompany set to false
     */
    public CompanyRequestModification(String studentId, Long serviceId,
                                      int hoursPerWeek, int totalHours,
                                      double pricePerHour, boolean acceptedByCompany) {
        this.studentId = studentId;
        this.serviceId = serviceId;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.acceptedByCompany = acceptedByCompany;
    }
}
