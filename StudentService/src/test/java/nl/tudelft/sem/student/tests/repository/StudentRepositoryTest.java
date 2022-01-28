package nl.tudelft.sem.student.tests.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// DON'T RUN THE TESTS SEPARATELY!
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentRepositoryTest {

    private final Student s1;
    private final Student s2;
    private final Student s3;
    @Autowired
    private StudentRepository studentRepository;

    /**
     * Generate three students to be tested.
     */
    public StudentRepositoryTest() {
        s1 = new Student("someNetID", "Matthijs");
        s2 = new Student("anotherNetID", "Marijn");
        s3 = new Student(null, "Denis");
    }

    @Test
    @Order(1)
    public void testStudentSequenceGenerator() {
        studentRepository.save(s1); //netId = "someNetID"
        studentRepository.saveAndFlush(s2); //netId = "anotherNetID"
        List<Student> students = studentRepository.findAll();
        assertEquals("someNetID", students.get(0).getNetId());
        assertEquals("anotherNetID", students.get(1).getNetId());
        assertTrue(studentRepository.existsById("someNetID"));
        assertTrue(studentRepository.existsById("anotherNetID"));
    }

    @Test
    @Order(2)
    public void testInvalidNetId() {
        studentRepository.saveAndFlush(s1);
        Student student = studentRepository.findStudentByNetId(s2.getNetId());
        assertNull(student);
    }
}
