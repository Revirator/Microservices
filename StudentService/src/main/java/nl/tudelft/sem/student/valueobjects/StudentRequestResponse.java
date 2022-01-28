package nl.tudelft.sem.student.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.tudelft.sem.student.entity.Student;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestResponse {
    private Student student;
    private StudentRequest studentRequest;
}
