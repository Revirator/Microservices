package nl.tudelft.sem.company.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestChange {
    private int hoursPerWeek = -1;
    private int totalHours = -1;
    private int salaryPerHour = -1;
    private List<String> requirements = null;
}