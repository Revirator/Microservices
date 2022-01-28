package nl.tudelft.sem.company.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.company.entity.Company;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModificationResponse {
    private Company company;
    private List<ContractModification> contractModifications;
}
