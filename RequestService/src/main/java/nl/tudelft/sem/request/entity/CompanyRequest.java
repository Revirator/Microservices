package nl.tudelft.sem.request.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequest extends Request {

    private String companyId;
    @ElementCollection
    private List<String> requirements;
    // Will be true if the company agrees to accept the request AFTER the student has accepted the
    // companies request!
    private boolean isAcceptedByCompany;
    // The ID's of the students that accepted the request.
    @ElementCollection
    private List<String> studentIdCandidates;
    // The ID of the student the company decided to work with.
    private String studentId;
    private String targetStudentId;

    /**
     * Constructor for the Company Request class.
     *
     * @param serviceId the id of that specific service
     * @param hoursPerWeek the number of hours per week requested
     * @param totalHours the number of hours requested
     * @param salaryPerHour the salary offered
     * @param companyId the id of the company
     * @param requirements the list of requirements needed for the job
     */
    public CompanyRequest(Long serviceId, int hoursPerWeek, int totalHours, double salaryPerHour,
                          String companyId, List<String> requirements) {
        super(serviceId, hoursPerWeek, totalHours, salaryPerHour);
        this.companyId = companyId;
        this.requirements = requirements;
        this.isAcceptedByCompany = false;
        this.studentIdCandidates = new ArrayList<>();
    }

    /**
     * Constructor for the Company Request class.
     *
     * @param hoursPerWeek the number of hours per week requested
     * @param totalHours the number of hours requested
     * @param salaryPerHour the salary offered
     * @param companyId the id of the company
     * @param requirements the list of requirements needed for the job
     */
    public CompanyRequest(int hoursPerWeek, int totalHours, double salaryPerHour, String companyId,
                          List<String> requirements) {
        super(hoursPerWeek, totalHours, salaryPerHour);
        this.companyId = companyId;
        this.requirements = requirements;
        this.isAcceptedByCompany = false;
        this.studentIdCandidates = new ArrayList<>();
    }

    /**
     * Add the NetID of a student to the list of candidates.
     *
     * @param studentId the ID of the student
     */
    public void addStudentId(String studentId) {
        List<String> candidates = new ArrayList<>(this.studentIdCandidates);
        candidates.add(studentId);
        this.studentIdCandidates = candidates;
    }

    /**
     * Remove the NetID of a student from the list of candidates.
     *
     * @param studentId the ID of the student
     */
    public void removeStudentId(String studentId) {
        List<String> candidates = new ArrayList<>(this.studentIdCandidates);
        candidates.remove(studentId);
        this.studentIdCandidates = candidates;
    }

    /**
     * Constructor for the request class without ID for testing purposes.
     *
     * @param hoursPerWeek the number of hours per week
     * @param totalHours the number of hours
     * @param salaryPerHour the salary
     */
    public CompanyRequest(int hoursPerWeek, int totalHours, double salaryPerHour) {
        super(hoursPerWeek, totalHours, salaryPerHour);
    }
}
