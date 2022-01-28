package nl.tudelft.sem.company.valueobjects;

import java.util.List;
import javax.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {
    private Long serviceId;
    private int hoursPerWeek;
    private int totalHours;
    private int salaryPerHour;
    private String companyId;
    private List<String> requirements;
    private List<String> studentIdCandidates;
    private boolean isAcceptedByCompany;
    private String studentId;
    private String targetStudentId;

    /**
     * Basic constructor without targetStudentId for sustainability.
     *
     * @param serviceId serviceId
     * @param hoursPerWeek hoursPerWeek
     * @param totalHours totalHours
     * @param salaryPerHour salaryPerHour
     * @param companyId companyId
     * @param requirements requirements
     * @param isAcceptedByCompany isAcceptedByCompany
     * @param studentId studentId
     */
    public CompanyRequest(Long serviceId, int hoursPerWeek, int totalHours, int salaryPerHour,
                          String companyId, List<String> requirements, boolean isAcceptedByCompany,
                          String studentId) {
        this.serviceId = serviceId;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.salaryPerHour = salaryPerHour;
        this.companyId = companyId;
        this.requirements = requirements;
        this.isAcceptedByCompany = isAcceptedByCompany;
        this.studentId = studentId;
    }
}
