package nl.tudelft.sem.request.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponseStudent {
    private List<Contract> contracts;
}

