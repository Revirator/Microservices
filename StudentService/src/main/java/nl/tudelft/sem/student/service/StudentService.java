package nl.tudelft.sem.student.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import nl.tudelft.sem.student.valueobjects.Company;
import nl.tudelft.sem.student.valueobjects.CompanyRequest;
import nl.tudelft.sem.student.valueobjects.CompanyRequestModification;
import nl.tudelft.sem.student.valueobjects.CompanyRequestResponse;
import nl.tudelft.sem.student.valueobjects.Contract;
import nl.tudelft.sem.student.valueobjects.ContractModification;
import nl.tudelft.sem.student.valueobjects.Feedback;
import nl.tudelft.sem.student.valueobjects.FeedbackResponse;
import nl.tudelft.sem.student.valueobjects.FilterTag;
import nl.tudelft.sem.student.valueobjects.ModificationResponse;
import nl.tudelft.sem.student.valueobjects.StudentRequest;
import nl.tudelft.sem.student.valueobjects.StudentRequestChange;
import nl.tudelft.sem.student.valueobjects.StudentRequestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Slf4j
public class StudentService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new GsonBuilder().create();

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    StudentRepository studentRepository;

    //CONSTRUCTOR FOR TESTING PURPOSES ONLY TO MOCK REST TEMPLATE!
    public StudentService(RestTemplate restTemplate, StudentRepository studentRepository) {
        this.restTemplate = restTemplate;
        this.studentRepository = studentRepository;
    }

    //*************HELPER METHODS***********

    /**
     * Creates a request with a JSON body.
     *
     * @param json The JSON body
     * @return The HTTP request to be sent.
     */
    public HttpEntity<String> requestCreator(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    /**
     * Converts response to a List of type T.
     *
     * @param response response
     * @param className className
     * @param <T> T
     * @return returnList
     */
    public <T> List<T> responseToList(List<LinkedHashMap> response, Class<T> className) {
        List<T> returnList = new ArrayList<>();
        for (LinkedHashMap o : response) {
            T obj = mapper.convertValue(o, className);
            returnList.add(obj);
        }
        return returnList;
    }

    //**********STUDENT API ENDPOINTS**************

    /**
     * Receives a request from the controller and sends it to the repository
     * to save a student in the database.
     *
     * @param studentId the netID of the student
     * @param student the student to save
     * @return new Student object with netId and name
     */
    public Student saveStudent(String studentId, Student student) {
        student = new Student(studentId, student.getName());
        return studentRepository.save(student);
    }

    /**
     * Receives a request from the controller and sends it to the repository
     * to find a student in the database.
     *
     * @param netId the ID to search for
     * @return new Student object with netId and name
     */
    public Student findStudentByNetId(String netId) {
        return studentRepository.findStudentByNetId(netId);
    }

    /**
     * Receives a request from the controller
     * and sends it to the repository.
     *
     * @return a List of all the students in the database
     */
    public List<Student> getAll() {
        return studentRepository.findAll();
    }



}

