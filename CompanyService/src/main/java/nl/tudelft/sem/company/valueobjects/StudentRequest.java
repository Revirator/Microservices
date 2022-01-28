package nl.tudelft.sem.company.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    private Long serviceId;
    private int hoursPerWeek;
    private int totalHours;
    private int salaryPerHour;
    private String studentId;
    private List<String> expertise;
    private boolean isAcceptedByStudent;
    private String companyId;
}
