package nl.tudelft.sem.request.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.repository.StudentRequestRepository;
import nl.tudelft.sem.request.valueobjects.Contract;
import nl.tudelft.sem.request.valueobjects.StudentRequestChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StudentRequestService extends RequestService {

    @Autowired
    private StudentRequestRepository studentRequestRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Receives the new studentRequest
     * and forwards it to the repository.
     *
     * @param studentRequest the new studentRequest made by either Student
     * @return new StudentRequest Instance
     */
    public StudentRequest saveStudentRequest(StudentRequest studentRequest) {
        return studentRequestRepository.save(studentRequest);
    }

    /**
     * Used to get all the student requests.
     *
     * @return a list of all the student requests in the database
     */
    public List<StudentRequest> getAllStudentRequests() {
        return studentRequestRepository.findAll();
    }

    /**
     * Returns the request made by a particular student.
     *
     * @param studentId the id of the student
     * @return the request made by the given student.
     */
    public StudentRequest findRequestByStudent(String studentId) {
        return studentRequestRepository.findByStudentId(studentId);
    }

    /**
     * Deletes a student service request from the database.
     *
     * @param studentId - the Id of the student who made the service request.
     * @return the deleted service request.
     */
    public StudentRequest deleteStudentRequest(String studentId) {
        StudentRequest request = studentRequestRepository.findByStudentId(studentId);
        if (request != null) {
            studentRequestRepository.delete(request);
        }
        return request;
    }

    /**
     * Updates the parameters of a student's request.
     *
     * @param studentId - the id of the student.
     * @param requestData - the changes to the request data.
     * @return the updated request.
     */
    public StudentRequest updateStudentRequest(String studentId, StudentRequestChange requestData) {
        StudentRequest request = studentRequestRepository.findByStudentId(studentId);
        if (request == null) {
            return null;
        }
        if (requestData.getExpertise() != null) {
            request.setExpertise(requestData.getExpertise());
        }
        if (requestData.getSalaryPerHour() != -1) {
            request.setSalaryPerHour(requestData.getSalaryPerHour());
        }
        if (requestData.getTotalHours() != -1) {
            request.setTotalHours(requestData.getTotalHours());
        }
        if (requestData.getHoursPerWeek() != -1) {
            request.setHoursPerWeek(requestData.getHoursPerWeek());
        }
        studentRequestRepository.save(request);
        return studentRequestRepository.findByStudentId(studentId);
    }

    /**
     * Accepts a request made by a student as a company.
     *
     * @param companyId The ID of the company accepting the studentRequest
     * @param serviceId The ID of the service to be accepted.
     * @return The StudentRequest that is accepted.
     */
    public StudentRequest acceptStudentRequest(String companyId, Long serviceId) {
        StudentRequest r = studentRequestRepository.findByServiceId(serviceId);
        //        if (r == null) {
        //            throw new NoSuchElementException
        //            ("A service with ID "+ serviceId+" does not exist!");

        //        }
        // Since the companyId field will not be null now, it indicates the student request has
        // been accepted by a company.
        r.setCompanyId(companyId);
        return studentRequestRepository.saveAndFlush(r);
    }

    /**
     * The student accepts its own request that it put up, assuming a company also accepted the
     * terms and then a contract is generated.
     *
     * @param studentId The ID of the student
     * @param serviceId The ID of the request service.
     * @return The company request
     */
    public StudentRequest acceptOwnRequestStudent(String studentId, Long serviceId) {
        StudentRequest r = studentRequestRepository.findByServiceId(serviceId);
        r.setAcceptedByStudent(true);
        //Generate contract
        Contract contract = new Contract();
        contract.setHoursPerWeek(r.getHoursPerWeek());
        contract.setTotalHours(r.getTotalHours());
        contract.setPricePerHour(r.getSalaryPerHour());
        contract.setCompanyId(r.getCompanyId());
        contract.setStudentId(r.getStudentId());
        contract.setTerminated(false);

        //Send contract to contract microservice
        super.sendGeneratedContract(contract);
        studentRequestRepository.delete(r);
        return r;
    }

    /**
     * Rejects the student as a company (The company does not want to work with a certain student).
     *
     * @param serviceId The ID of the service
     * @return The rejected CompanyRequest
     */
    public StudentRequest rejectCompanyAsStudent(Long serviceId) {
        StudentRequest r = studentRequestRepository.findByServiceId(serviceId);
        // Since the companyId field will be null now, it indicates the student rejected the company
        r.setCompanyId("");
        r.setAcceptedByStudent(false);
        return studentRequestRepository.saveAndFlush(r);
    }

    /**
     * Finds a Student Request by service ID.
     *
     * @param serviceId The ID of the service/request.
     * @return The Student Request.
     */
    public StudentRequest findStudentRequestByServiceId(Long serviceId) {
        return studentRequestRepository.findByServiceId(serviceId);
    }

    /**
     * The method searchExpertiseStudents expects a expertise (String)
     * and will return a List of students. The students which have that expertise
     * listed in their expertise List in the StudentRequest will then be shown.
     *
     * @param expertise expert skill or knowledge on which a company wants to filter students.
     * @return a List of type studentRequest with all the students that have the defined expertise
     */
    public List<StudentRequest> searchExpertiseStudents(String expertise) {
        log.info("Inside searchExpertiseStudents of CompanyService");
        List<StudentRequest> students = new ArrayList<>();
        List<StudentRequest> requests = studentRequestRepository.findAll();
        for (StudentRequest s : requests) {
            List<String> expertises = s.getExpertise();

            if (expertises.contains(expertise)) {
                students.add(s);
            }
        }
        return students;
    }

    /**
     * Used to get all the student requests filtered by total hours.
     * With a specific interval.
     *
     * @param startOfInterval the start of that specific interval
     * @param endOfInterval   the end of that specific interval
     * @return a list of all the requests for that specific filter
     */
    public List<StudentRequest> findAllStudentRequestsByHours(int startOfInterval,
                                                              int endOfInterval) {
        List<StudentRequest> requests = studentRequestRepository
            .findAllByHoursPerWeekBetween(startOfInterval, endOfInterval);
        List<StudentRequest> result = new ArrayList<>();
        for (StudentRequest s : requests) {
            if (!s.isAcceptedByStudent()) {
                StudentRequest o = mapper.convertValue(s, StudentRequest.class);
                result.add(o);
            }
        }
        return result;
    }

}

