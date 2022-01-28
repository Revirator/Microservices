package nl.tudelft.sem.student.valueobjects;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest {
    @Setter(AccessLevel.NONE)
    private Long serviceId;
    private int hoursPerWeek;
    private int totalHours;
    private int salaryPerHour;
    private String studentId;
    private List<String> expertise;
    private String companyId;
    private boolean acceptedByStudent;
}
