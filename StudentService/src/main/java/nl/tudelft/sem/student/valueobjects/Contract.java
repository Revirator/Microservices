package nl.tudelft.sem.student.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contract {
    private Long contractId;
    private int hoursPerWeek;
    private int totalHours;
    private double pricePerHour;
    private String studentId;
    private String companyId;
    private boolean terminated;
}
