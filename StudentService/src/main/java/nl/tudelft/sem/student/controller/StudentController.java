package nl.tudelft.sem.student.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Slf4j
public class StudentController {

    private final StudentService studentService;
    StudentServiceContract studentServiceContract;
    StudentServiceFeedback studentServiceFeedback;
    StudentServiceRequest studentServiceRequest;

    /**
     * Constructor for the studentController.
     *
     * @param studentService studentService
     * @param studentServiceContract studentServiceContract
     * @param studentServiceFeedback studentServiceFeedback
     * @param studentServiceRequest studentServiceReqeust
     */
    @Autowired
    public StudentController(StudentService studentService,
                             StudentServiceContract studentServiceContract,
                             StudentServiceFeedback studentServiceFeedback,
                             StudentServiceRequest studentServiceRequest) {
        this.studentService = studentService;
        this.studentServiceContract = studentServiceContract;
        this.studentServiceFeedback = studentServiceFeedback;
        this.studentServiceRequest = studentServiceRequest;
    }

    /**
     * Used in testing; to get all the students from the database.
     * Cannot be invoked by the gateway but only by another microservice.
     *
     * @return a List of all the students in the database
     */
    @GetMapping("/")
    @ResponseBody
    public List<Student> getAll() {
        return studentService.getAll();
    }

    /**
     * Saves a student to the database.
     * Cannot be invoked by the gateway but only by another microservice.
     *
     * @param studentId the netID of the student
     * @param student the object to save
     * @return ResponseEntity to tell the client how the request went
     */
    @PostMapping("/{studentId}/")
    public Student saveStudent(@PathVariable String studentId,
                               @RequestBody Student student) {
        return studentService.saveStudent(studentId, student);
    }

    /**
     * Finds a student in the database using their netID.
     *
     * @param netId the netID from the student to find
     * @return new Student object with data from the database
     */
    @GetMapping("/{netId}/")
    public Student findStudentByNetId(@PathVariable("netId") String netId) {
        return studentService.findStudentByNetId(netId);
    }

}
