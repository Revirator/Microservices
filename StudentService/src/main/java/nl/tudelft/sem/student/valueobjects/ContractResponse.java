package nl.tudelft.sem.student.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.student.entity.Student;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractResponse {
    private Student student;
    private List<Contract> contracts;
}
