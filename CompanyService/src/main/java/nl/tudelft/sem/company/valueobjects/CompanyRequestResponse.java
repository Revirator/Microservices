package nl.tudelft.sem.company.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequestResponse {
    private Student student;
    private CompanyRequest companyRequest;
}
