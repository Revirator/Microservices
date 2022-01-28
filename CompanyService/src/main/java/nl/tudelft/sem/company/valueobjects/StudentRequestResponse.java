package nl.tudelft.sem.company.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.company.entity.Company;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestResponse {
    private Company company;
    private StudentRequest studentRequest;
}
