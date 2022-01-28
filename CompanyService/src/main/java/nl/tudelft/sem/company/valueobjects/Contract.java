package nl.tudelft.sem.company.valueobjects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
