package nl.tudelft.sem.student.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestChange {
    private int hoursPerWeek = -1;
    private int totalHours = -1;
    private int salaryPerHour = -1;
    private List<String> expertise = null;
}
