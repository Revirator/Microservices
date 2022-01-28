package nl.tudelft.sem.student.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
import nl.tudelft.sem.student.valueobjects.CompanyRequest;
import nl.tudelft.sem.student.valueobjects.CompanyRequestModification;
import nl.tudelft.sem.student.valueobjects.CompanyRequestResponse;
import nl.tudelft.sem.student.valueobjects.FilterTag;
import nl.tudelft.sem.student.valueobjects.StudentRequest;
import nl.tudelft.sem.student.valueobjects.StudentRequestChange;
import nl.tudelft.sem.student.valueobjects.StudentRequestResponse;
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
@RequestMapping("/student")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Slf4j
public class StudentControllerRequest extends StudentController {

    private final String studentString = "Student ";

    public StudentControllerRequest(StudentService studentService,
                                    StudentServiceContract studentServiceContract,
                                    StudentServiceFeedback studentServiceFeedback,
                                    StudentServiceRequest studentServiceRequest) {
        super(studentService, studentServiceContract, studentServiceFeedback,
                studentServiceRequest);
    }

    /**
     * Sends a request to the Request microservice to get all the requests made by companies.
     *
     * @return a List of all the requests that are made by companies
     */
    @GetMapping("/{studentId}/companyRequests")
    @ResponseBody
    public List<CompanyRequest> getAllRequests(@PathVariable String studentId) {
        log.info(studentString + studentId + " requests all company offers");
        return studentServiceRequest.getAllRequests();
    }

    /**
     * Sends a request to the Request microservice to get all the requests made by a company.
     *
     * @return a List of all the requests that are made by a company
     */
    @GetMapping("/{studentId}/request/{companyId}")
    @ResponseBody
    public List<CompanyRequest> getRequestByCompanyId(@PathVariable String studentId,
                                                      @PathVariable String companyId) {
        log.info(studentString + studentId + " requests offers from " + companyId);
        return studentServiceRequest.getRequestByCompanyId(companyId);
    }

    /**
     * Gets a student's service request.
     *
     * @param studentId - the net id of the student whose request to get.
     * @return the student's request.
     */
    @GetMapping("/{studentId}/request")
    @ResponseBody
    public StudentRequest getStudentRequestByStudentId(@PathVariable String studentId) {
        log.info("Student " + studentId + " is fetching their service request.");
        return studentServiceRequest.getStudentRequestByStudentId(studentId);
    }

    /**
     * Sends a request to the Request microservice
     * to get all the specific requests filtered.
     * Using the specific description of the filter.
     *
     * @param filterTag the filter tag in order to determine the specific filter to apply
     * @return List of requests for that interval
     */
    @PostMapping("/{studentId}/request/filter")
    @ResponseBody
    public List<CompanyRequest> filterRequests(@PathVariable String studentId,
                                               @RequestBody FilterTag filterTag) {

        log.info(studentString + studentId + " company requests with the "
                + filterTag.getTypeOfFiltering() + " between " + filterTag.getStartOfTheInterval()
                + " and " + filterTag.getEndOfTheInterval());
        return studentServiceRequest.applyFilter(filterTag);
    }

    /**
     * Creates a service request by the student.
     *
     * @param studentId - the Net ID of the student making the request.
     * @param request   - the data included in the request.
     * @return the newly-generated service request.
     */
    @PostMapping("/{studentId}/request")
    @ResponseBody
    public StudentRequest postStudentRequest(@PathVariable String studentId,
                                             @RequestBody StudentRequest request) {
        log.info("Student " + studentId + " is creating a new service request.");
        return studentServiceRequest.postStudentRequest(studentId, request);
    }

    /**
     * Deletes a student's service request.
     *
     * @param studentId - the net id of the student whose request to delete.
     * @return the deleted request.
     */
    @DeleteMapping("/{studentId}/request")
    @ResponseBody
    public StudentRequest deleteStudentRequest(@PathVariable String studentId) {
        log.info("Student " + studentId + " is deleting their service request.");
        return studentServiceRequest.deleteStudentRequest(studentId);
    }

    /**
     * Updates the student's request parameters without changing the student or service id.
     *
     * @param studentId   - the netId of the student whose request to update.
     * @param requestData - the wanted new changes to the request data.
     * @return the updated request.
     */
    @PutMapping("/{studentId}/request")
    @ResponseBody
    public StudentRequest updateStudentRequest(@PathVariable String studentId,
                                               @RequestBody StudentRequestChange requestData) {
        log.info("Student " + studentId + " is updating their service request.");
        return studentServiceRequest.updateStudentRequest(studentId, requestData);
    }

    /**
     * Accepts the students own Request, assuming the company has accepted their Request.
     *
     * @param studentId The ID of the student.
     * @param serviceId The ID of the request/service.
     * @return The StudentRequestResponse.
     */
    @PostMapping("/{studentId}/request/acceptOwn/{serviceId}")
    @ResponseBody
    public StudentRequestResponse acceptByStudent(@PathVariable String studentId,
                                                  @PathVariable Long serviceId) {
        return studentServiceRequest.acceptByStudent(studentId, serviceId);
    }

    /**
     * Accepts the request of a company as a student.
     *
     * @param studentId The ID of the student.
     * @param serviceId The ID of the request/service.
     * @return The CompanyRequestResponse.
     */
    @PostMapping("/{studentId}/request/accept/{serviceId}")
    @ResponseBody
    public CompanyRequestResponse acceptCompanyRequest(@PathVariable String studentId,
                                                       @PathVariable Long serviceId) {
        return studentServiceRequest.acceptCompanyRequest(studentId, serviceId);
    }

    /**
     * Rejects the request made by a company to their own Request.
     *
     * @param studentId The ID of the student.
     * @param serviceId The ID of the service.
     * @return The StudentRequestResponse.
     */
    @PostMapping("/{studentId}/request/reject/{serviceId}")
    @ResponseBody
    public StudentRequestResponse rejectCompanyRequest(@PathVariable String studentId,
                                                       @PathVariable Long serviceId) {
        return studentServiceRequest.rejectCompanyRequest(studentId, serviceId);
    }

    /**
     * Sends an object with all the changes that a student wants
     * to make to a job request.
     *
     * @param studentId the ID of the student.
     * @param serviceId the ID of the service.
     * @param modification the changes that the student wants to make.
     * @return the CompanyRequestModification.
     */
    @PostMapping("/{studentId}/request/change/{serviceId}")
    @ResponseBody
    public CompanyRequestModification requestChangesToCompanyRequest(
            @PathVariable String studentId,
            @PathVariable Long serviceId,
            @RequestBody CompanyRequestModification modification) {
        return studentServiceRequest
                .requestChangesToCompanyRequest(studentId, serviceId, modification);
    }

    /**
     * Sends a request to the service to extract all targeted
     * requests from the Request microservice.
     *
     * @param studentId the student to get the targeted requests for.
     * @return a list of targeted requests
     */
    @GetMapping("/{studentId}/request/getAll/targeted")
    public List<CompanyRequest> getTargetedRequest(@PathVariable String studentId) {
        log.info(studentId + " wants to see all targeted requests");
        return studentServiceRequest.getTargetedRequest(studentId);
    }

}
