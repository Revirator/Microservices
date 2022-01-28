package nl.tudelft.sem.request.entity;

import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest extends Request {
    private String studentId;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> expertise;
    // Will be true if the student agrees to accept the request AFTER the company has accepted the
    // students request!
    private boolean isAcceptedByStudent;
    // The ID of the company that accepted the request.
    private String companyId;

    /**
     * Constructor for the Student Request class.
     *
     * @param serviceId the id of that specific request
     * @param hoursPerWeek the number of available hours per week
     * @param totalHours the number of available hours
     * @param salaryPerHour the salary requested
     * @param studentId the id of the student
     * @param expertise the list of prior experience
     */
    public StudentRequest(Long serviceId, int hoursPerWeek, int totalHours, double salaryPerHour,
                          String studentId, List<String> expertise) {
        super(serviceId, hoursPerWeek, totalHours, salaryPerHour);
        this.studentId = studentId;
        this.expertise = expertise;
        this.isAcceptedByStudent = false;
        this.companyId = "";
    }

    /**
     * Constructor for the Student Request class.
     *
     * @param hoursPerWeek the number of available hours per week
     * @param totalHours the number of available hours
     * @param salaryPerHour the salary requested
     * @param studentId the id of the student
     * @param expertise the list of prior experience
     */
    public StudentRequest(int hoursPerWeek, int totalHours, double salaryPerHour, String studentId,
                          List<String> expertise) {
        super(hoursPerWeek, totalHours, salaryPerHour);
        this.studentId = studentId;
        this.expertise = expertise;
        this.isAcceptedByStudent = false;
        this.companyId = "";
    }
}
