package nl.tudelft.sem.request.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.service.CompanyRequestService;
import nl.tudelft.sem.request.service.StudentRequestService;
import nl.tudelft.sem.request.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.request.valueobjects.FilterTag;
import nl.tudelft.sem.request.valueobjects.StudentRequestChange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request")
@Slf4j
public class RequestController {

    private final CompanyRequestService companyRequestService;
    private final StudentRequestService studentRequestService;

    @Autowired
    public RequestController(CompanyRequestService companyRequestService,
                             StudentRequestService studentRequestService) {
        this.companyRequestService = companyRequestService;
        this.studentRequestService = studentRequestService;
    }

    /**
     * Returns all student requests in the database.
     *
     * @return a list of all the requests
     */
    @GetMapping("/student")
    @ResponseBody
    public List<StudentRequest> getAllStudentRequests() {
        return studentRequestService.getAllStudentRequests();
    }

    /**
     * Returns a student request by service ID.
     *
     * @return The Student Request
     */
    @GetMapping("/student/serviceId/{serviceId}")
    @ResponseBody
    public StudentRequest findStudentRequestByServiceId(@PathVariable Long serviceId) {
        return studentRequestService.findStudentRequestByServiceId(serviceId);
    }

    /**
     * Finds a company request by its service ID.
     *
     * @return The Company Request
     */
    @GetMapping("/company/service/{serviceId}")
    @ResponseBody
    public CompanyRequest findCompanyRequestByServiceId(@PathVariable Long serviceId) {
        return companyRequestService.findCompanyRequestByServiceId(serviceId);
    }

    /**
     * Returns all company requests in the database.
     *
     * @return a list of all the requests
     */
    @GetMapping("/company")
    @ResponseBody
    public List<CompanyRequest> getAllCompanyRequests() {
        return companyRequestService.getAllCompanyRequests();
    }

    /**
     * Receives the data for a student Request that needs to be saved to the database.
     * Forwards the studentRequest to the service class.
     *
     * @param request Student request
     * @return new studentRequest instance
     */
    @PostMapping("/student")
    @ResponseBody
    public StudentRequest saveRequest(@RequestBody StudentRequest request) {
        return studentRequestService.saveStudentRequest(request);
    }

    /**
     * Receives the data for a company Request that needs to be saved to the database.
     * Forwards the companyRequest to the service class.
     *
     * @param request CompanyRequest
     * @return new studentRequest instance
     */
    @PostMapping("/company")
    @ResponseBody
    public CompanyRequest saveRequest(@RequestBody CompanyRequest request) {
        return companyRequestService.saveCompanyRequest(request);
    }

    /**
     * Used to request the specific requests for a company
     * Using the id of that specific company.
     *
     * @param companyId the id of the Company
     * @return List of requests for that company (empty list if no requests)
     */
    @GetMapping("/company/{companyId}")
    @ResponseBody
    public List<CompanyRequest> getRequestsForCompany(@PathVariable String companyId) {
        return companyRequestService.findAllRequestByCompanyId(companyId);
    }

    /**
     * Used to request the specific company requests filtered.
     * Using the given description of filter.
     *
     * @param requestData the filter tag in order to determine the specific filter to apply
     * @return a List of all the company requests that correspond to that specific filter
     */
    @PostMapping("/company/filter")
    @ResponseBody
    public List<CompanyRequest> filterCompanyRequests(@RequestBody FilterTag requestData) {

        if (requestData.getStartOfTheInterval() <= requestData.getEndOfTheInterval()
            && requestData.getStartOfTheInterval() >= 0) {
            return filterCompanyRequestsBySalary(requestData);
        } else {
            return List.of();
        }

    }

    /**
     * Helper method to check if the type of filtering is salary.
     *
     * @param requestData the filter tag
     * @return a List of all the company requests that correspond to that specific filter
     */
    protected List<CompanyRequest> filterCompanyRequestsBySalary(FilterTag requestData) {
        if (requestData.getTypeOfFiltering().equals("salary")) {
            return companyRequestService.findAllCompanyRequestsBySalaryPerHour(
                requestData.getStartOfTheInterval(), requestData.getEndOfTheInterval());
        } else {
            return filterCompanyRequestsByHoursPerWeek(requestData);
        }
    }

    /**
     * Helper method to check if the type of filtering is hoursPerWeek.
     *
     * @param requestData the filter tag
     * @return a List of all the company requests that correspond to that specific filter
     */
    protected List<CompanyRequest> filterCompanyRequestsByHoursPerWeek(FilterTag requestData) {
        if (requestData.getTypeOfFiltering().equals("hoursPerWeek")) {
            return companyRequestService.findAllCompanyRequestsByHoursPerWeek(
                requestData.getStartOfTheInterval(), requestData.getEndOfTheInterval());
        } else {
            return filterCompanyRequestsByTotalHours(requestData);
        }
    }

    /**
     * Helper method to check if the type of filtering is totalHours.
     *
     * @param requestData the filter tag
     * @return a List of all the company requests that correspond to that specific filter
     */
    protected List<CompanyRequest> filterCompanyRequestsByTotalHours(FilterTag requestData) {
        if (requestData.getTypeOfFiltering().equals("totalHours")) {
            return companyRequestService.findAllCompanyRequestsByTotalHours(
                requestData.getStartOfTheInterval(), requestData.getEndOfTheInterval());
        }
        return List.of();
    }

    /**
     * Used to request the specific requests filtered.
     * Using the given description of filter.
     *
     * @param requestData the filter tag in order to determine the specific filter to apply
     * @return a List of all the student requests that correspond to that specific filter
     */
    @PostMapping("/student/filter")
    @ResponseBody
    public List<StudentRequest> filterStudentRequests(@RequestBody FilterTag requestData) {
        if (requestData.getTypeOfFiltering().equals("hoursPerWeek")) {
            return studentRequestService.findAllStudentRequestsByHours(
                requestData.getStartOfTheInterval(), requestData.getEndOfTheInterval());
        }
        return List.of();
    }

    /**
     * Used to request the specific requests for a student
     * Using the id of that specific student.
     *
     * @param studentId the id of the student
     * @return List of targeted requests for that student (empty list if no requests)
     */
    @GetMapping("/student/targeted/{studentId}")
    @ResponseBody
    public List<CompanyRequest> getTargetedRequestsForStudent(@PathVariable String studentId) {
        return companyRequestService.findTargetedRequestsForStudent(studentId);
    }

    /**
     * Used to post a request that targets a specific student.
     * Using the id of that specific student.
     *
     * @param studentId      the id of the student that is targeted
     * @param companyRequest the request to target the student with
     * @return the request once it has been saved
     */
    @PostMapping("/company/targeted/{studentId}")
    @ResponseBody
    public CompanyRequest saveTargetedCompanyRequest(@PathVariable String studentId,
                                                     @RequestBody CompanyRequest companyRequest) {
        return companyRequestService.saveTargetedCompanyRequest(companyRequest, studentId);
    }

    /**
     * Returns the request made by a particular student.
     *
     * @param studentId the id of the student.
     * @return the request made by the given student.
     */
    @GetMapping("/student/{studentId}")
    @ResponseBody
    public StudentRequest getRequestForStudent(@PathVariable String studentId) {
        return studentRequestService.findRequestByStudent(studentId);
    }

    /**
     * Accept a request sent out by company as as a student.
     *
     * @param serviceId The ID of the service the company put out.
     * @return The accepted CompanyRequest
     */
    @PostMapping("/company/{studentId}/{serviceId}")
    @ResponseBody
    public CompanyRequest acceptCompanyRequest(@PathVariable String studentId,
                                               @PathVariable Long serviceId) {
        return companyRequestService.acceptCompanyRequest(studentId, serviceId);
    }

    /**
     * Rejects the student as a company (The company does not want to work with a certain student).
     *
     * @param serviceId The ID of the service
     * @return The rejected CompanyRequest
     */
    @PostMapping("/company/reject/{serviceId}/{studentId}")
    public CompanyRequest rejectStudentAsCompany(@PathVariable Long serviceId,
                                                 @PathVariable String studentId) {
        return companyRequestService.rejectStudentAsCompany(serviceId, studentId);
    }

    /**
     * Accept a request sent out by student as as a company.
     *
     * @param serviceId The ID of the service the student put out.
     * @return The accepted StudentRequest
     */
    @PostMapping("/student/accept/{companyId}/{serviceId}")
    @ResponseBody
    public StudentRequest acceptStudentRequest(@PathVariable String companyId,
                                               @PathVariable Long serviceId) {
        return studentRequestService.acceptStudentRequest(companyId, serviceId);
    }

    /**
     * Rejects the company as a student (The student does not want to work with a certain company).
     *
     * @param serviceId The ID of the service
     * @return The rejected StudentRequest
     */
    @PostMapping("/student/reject/{serviceId}")
    public StudentRequest rejectCompanyAsStudent(@PathVariable Long serviceId) {
        return studentRequestService.rejectCompanyAsStudent(serviceId);
    }

    /**
     * Company accepts its own request, assuming that a student has also accepted it, generating
     * a contract.
     *
     * @param companyId The ID of the company
     * @param serviceId The ID of the service
     * @return The accepted CompanyRequest.
     */
    @PostMapping("/company/acceptOwn/{companyId}/{serviceId}/{studentId}")
    public CompanyRequest acceptByCompany(@PathVariable String companyId,
                                          @PathVariable Long serviceId,
                                          @PathVariable String studentId) {
        return companyRequestService.acceptOwnRequestCompany(companyId, studentId, serviceId);
    }

    /**
     * Student accepts its own request, assuming that a company has also accepted it, generating
     * a contract.
     *
     * @param studentId The ID of the student
     * @param serviceId The ID of the service
     * @return The accepted StudentRequest.
     */
    @PostMapping("/student/acceptOwn/{studentId}/{serviceId}")
    public StudentRequest acceptByStudent(@PathVariable String studentId,
                                          @PathVariable Long serviceId) {
        return studentRequestService.acceptOwnRequestStudent(studentId, serviceId);
    }

    /**
     * Deletes all job requests from the database from specific company.
     *
     * @param companyId - the Id of the company who made the job request.
     * @return the list of deleted job requests.
     */
    @DeleteMapping("/company/{companyId}")
    @ResponseBody
    public List<CompanyRequest> deleteAllCompanyRequests(@PathVariable String companyId) {
        return companyRequestService.deleteAllCompanyRequests(companyId);
    }

    /**
     * Deletes certain job requests from the database from specific company.
     *
     * @param serviceId - the Id of the service that will be deleted.
     * @return the deleted job requests.
     */
    @DeleteMapping("/company/service/{serviceId}")
    @ResponseBody
    public CompanyRequest deleteCompanyRequest(@PathVariable Long serviceId) {
        return companyRequestService.deleteCompanyRequest(serviceId);
    }

    /**
     * Deletes a student service request from the database.
     *
     * @param studentId - the Id of the student who made the service request.
     * @return the deleted service request.
     */
    @DeleteMapping("/student/{studentId}")
    @ResponseBody
    public StudentRequest deleteStudentRequest(@PathVariable String studentId) {
        return studentRequestService.deleteStudentRequest(studentId);
    }

    /**
     * Updates the parameters of a student's request.
     *
     * @param studentId   - the id of the student.
     * @param requestData - the changes to the request data.
     * @return the updated request.
     */
    @PutMapping("/student/{studentId}")
    @ResponseBody
    public StudentRequest updateStudentRequest(@PathVariable String studentId,
                                               @RequestBody StudentRequestChange requestData) {
        return studentRequestService.updateStudentRequest(studentId, requestData);
    }

    /**
     * Updates the parameters of a company's request.
     *
     * @param serviceId   - the id of the service.
     * @param requestData - the changes to the request data.
     * @return the updated request.
     */
    @PutMapping("/company/service/{serviceId}")
    @ResponseBody
    public CompanyRequest updateCompanyRequest(@PathVariable Long serviceId,
                                               @RequestBody CompanyRequestChange requestData) {
        return companyRequestService.updateCompanyRequest(serviceId, requestData);
    }

    /**
     * Sends a GET request to the Request MicroService to receive all students
     * that have a certain expertise.
     *
     * @param expertise a string which indicates a expertise of a student
     * @return a List of all the students that match the expertise
     */
    @GetMapping("/company/search/{expertise}")
    @ResponseBody
    public List<StudentRequest> searchExpertiseStudents(@PathVariable String expertise) {
        log.info("Company requests requests from certain students based on expertise");
        return studentRequestService.searchExpertiseStudents(expertise);
    }

    /**
     * Returns the candidates for students who have accepted that company request.
     *
     * @param serviceId The ID of the service.
     * @return A list of student ID's for the company to choose from.
     */
    @GetMapping("/company/candidates/{serviceId}")
    @ResponseBody
    public List<String> getStudentIdCandidates(@PathVariable Long serviceId) {
        return companyRequestService.getStudentIdCandidates(serviceId);
    }
}

