package nl.tudelft.sem.company.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.service.CompanyService;
import nl.tudelft.sem.company.valueobjects.CompanyRequest;
import nl.tudelft.sem.company.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.company.valueobjects.CompanyRequestModification;
import nl.tudelft.sem.company.valueobjects.Contract;
import nl.tudelft.sem.company.valueobjects.ContractModification;
import nl.tudelft.sem.company.valueobjects.ContractResponse;
import nl.tudelft.sem.company.valueobjects.Feedback;
import nl.tudelft.sem.company.valueobjects.FeedbackResponse;
import nl.tudelft.sem.company.valueobjects.FilterTag;
import nl.tudelft.sem.company.valueobjects.ModificationResponse;
import nl.tudelft.sem.company.valueobjects.StudentRequest;
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
@RequestMapping("/company")
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * Purely for testing purposes.
     * Returns all companies in the database.
     *
     * @return a list of all the companies
     */
    @GetMapping("/")
    @ResponseBody
    public List<Company> getAll() {
        return companyService.getAll();
    }

    /**
     * Receives the Company that needs to be saved to the database.
     * Forwards the request to the service class.
     *
     * @param company the Company object
     * @return the company that is saved
     */
    @PostMapping("/")
    @ResponseBody
    public Company saveCompany(@RequestBody Company company) {
        log.info("Inside saveCompany method of CompanyController");
        return companyService.saveCompany(company);
    }

    /**
     * Handles the request asking for the Company for a specific name.
     * Forwards the request to the service class.
     *
     * @param companyId the username of the Company
     * @return Company object of class Company according to the username
     */
    @GetMapping("/{companyId}/")
    @ResponseBody
    public Company findCompanyByCompanyId(@PathVariable("companyId") String companyId) {
        log.info("Inside findCompanyByCompanyId method of CompanyController");
        return companyService.findCompanyByUsername(companyId);
    }

    /**
     * Sends a request to the Feedback Microservice to get the feedbacks for a student.
     *
     * @param companyId the ID of the company making the request
     * @param studentId the ID for the company for which we want the feedbacks
     * @return a List of all the feedbacks the student has received
     */
    @GetMapping("/{companyId}/feedback/{studentId}")
    @ResponseBody
    public FeedbackResponse getFeedbackForStudent(@PathVariable String companyId,
                                                  @PathVariable String studentId) {
        log.info(companyId + " requests feedback for " + studentId);
        return companyService.getFeedbackForStudent(studentId);
    }

    /**
     * Sends a request to the Feedback Microservice to post a new feedback for a company.
     *
     * @param companyId the ID of the company making the request
     * @param studentId the ID for the student for which we want to post the feedback
     * @param feedback  the new feedback to be posted for that student
     * @return a List of all the feedbacks the student has received including the new one
     */
    @PostMapping("/{companyId}/feedback/{studentId}")
    @ResponseBody
    public FeedbackResponse postFeedbackForStudent(@PathVariable String companyId,
                                                   @PathVariable String studentId,
                                                   @RequestBody Feedback feedback) {
        log.info(companyId + " posts feedback for " + studentId);
        return companyService.postFeedbackForStudent(studentId, feedback);
    }

    /**
     * Sends a request to the Request Microservice to post a new CompanyRequest for the company.
     *
     * @param companyId The ID of the company.
     * @param companyRequest The request the company wants to put up
     * @return The posted CompanyRequest
     */
    @PostMapping("/{companyId}/request")
    @ResponseBody
    public CompanyRequest postCompanyRequest(@PathVariable String companyId,
                                             @RequestBody CompanyRequest companyRequest) {
        log.info("Inside postCompanyRequest of CompanyController!");
        return companyService.postCompanyRequest(companyId, companyRequest);
    }

    /**
     * Sends a request to the Contract Microservice to get the ongoing Contracts of the company.
     *
     * @param companyId the ID of the company
     * @return a List of all the contracts the company has.
     */
    @GetMapping("/{companyId}/contracts")
    @ResponseBody
    public ContractResponse getContractsByCompanyId(@PathVariable String companyId) {
        log.info("Inside getContractsByCompanyId of CompanyController");
        return companyService.getContractsByCompanyId(companyId);
    }



    /**
     * Sends a request to the Request microservice to get all the requests made by students.
     *
     * @return a List of all the requests that are made by companies
     */
    @GetMapping("/{companyId}/studentRequests")
    @ResponseBody
    public List<StudentRequest> getAllRequests(@PathVariable String companyId) {
        log.info(companyId + " requests all studentRequest offers");
        return companyService.getAllRequests();
    }


    /**
     * Sends request to the Request microservice to accept the companies own request, assuming
     * that a student has already accepted it.
     *
     * @param companyId the ID of the company.
     * @param serviceId the ID of the service/request.
     * @return the accepted CompanyRequest.
     */
    @PostMapping("/{companyId}/request/acceptOwn/{serviceId}/{studentId}")
    @ResponseBody
    public CompanyRequest acceptByCompany(@PathVariable String companyId,
                                          @PathVariable Long serviceId,
                                          @PathVariable String studentId) {
        return companyService.acceptByCompany(companyId, serviceId, studentId);
    }

    /**
     * Sends request to the Request microservice to accept a student request as a company.
     *
     * @param companyId The ID of the company
     * @param serviceId The ID of the service/request.
     * @return The accepted StudentRequest.
     */
    @PostMapping("/{companyId}/request/accept/{serviceId}")
    @ResponseBody
    public StudentRequest acceptStudentRequest(@PathVariable String companyId,
                                               @PathVariable Long serviceId) {
        return companyService.acceptStudentRequest(companyId, serviceId);
    }

    /**
     * Sends a GET request to the Request MicroService to receive
     * all students that have a certain expertise.
     *
     * @param companyId the Id of the company
     * @param expertise a string which indicates a expertise of a student
     * @return a List of all the students that match the expertise
     */
    @GetMapping("/{companyId}/search/{expertise}")
    @ResponseBody
    public List<StudentRequest> searchExpertiseStudents(@PathVariable String companyId,
                                                        @PathVariable String expertise) {
        log.info(companyId + "requests requests from all students");
        return companyService.searchExpertiseStudents(expertise);
    }

    /**
     * Delete all companies job requests.
     *
     * @param companyId - the net id of the company whose requests to delete.
     * @return a list of the deleted requests.
     */
    @DeleteMapping("/{companyId}/deleteAll")
    @ResponseBody
    public List<CompanyRequest> deleteAllCompanyRequests(@PathVariable String companyId) {
        log.info("Company " + companyId + " is deleting all their job requests.");
        return companyService.deleteAllCompanyRequests(companyId);
    }

    /**
     * Delete certain company's job requests.
     *
     * @param companyId - the net id of the company whose request to delete.
     * @param serviceId - the Id of the service that will be deleted
     * @return the deleted request.
     */
    @DeleteMapping("/{companyId}/delete/{serviceId}")
    @ResponseBody
    public CompanyRequest deleteCompanyRequest(@PathVariable String companyId,
                                               @PathVariable Long serviceId) {
        log.info("Company " + companyId + " is deleting a job request.");
        return companyService.deleteCompanyRequest(companyId, serviceId);
    }

    /**
     * Updates the company's request parameters without changing the company or service id.
     *
     * @param companyId   - the netId of the student whose request to update.
     * @param requested - the wanted new changes to the request data.
     * @return the updated request.
     */
    @PutMapping("/{companyId}/update/{serviceId}")
    @ResponseBody
    public CompanyRequest updateCompanyRequest(@PathVariable String companyId,
                                               @PathVariable Long serviceId,
                                               @RequestBody CompanyRequestChange requested) {
        log.info("The Company " + companyId + " is updating their service request.");
        return companyService.updateCompanyRequest(companyId, serviceId, requested);
    }

    /** Sends request to the Contract microservice to accept a contractModification as a company.
    *
    * @param companyId      The ID of the company.
    * @param modificationId The ID of the modification.
    * @return the modified contract
    */
    @PostMapping("/{companyId}/modification/accepts/{modificationId}")
    @ResponseBody
    public Contract acceptModification(@PathVariable String companyId,
                                       @PathVariable Long modificationId) {
        return companyService.acceptModification(companyId, modificationId);

    }

    /**
     * Sends request to the Contract microservice to decline a contractModification as a company.
     *
     * @param companyId      The ID of the company
     * @param modificationId The ID of the modification.
     * @return the decline modification
     */
    @PostMapping("/{companyId}/modification/declines/{modificationId}")
    @ResponseBody
    public ContractModification declineModification(@PathVariable String companyId,
                                                    @PathVariable Long modificationId) {
        return companyService.declineModification(companyId, modificationId);

    }

    /**
     * Sends request to the Contract microservice to propose a contractModification as a company.
     *
     * @param companyId    The ID of the company
     * @param modification the modification to be proposed
     * @return the modification
     */
    @PostMapping("/{companyId}/modification/proposes")
    @ResponseBody
    public ContractModification proposeModification(@PathVariable String companyId, @RequestBody
        ContractModification modification) {
        return companyService.proposeModification(modification);

    }

    /**
     * Sends a request to the Contract Microservice to get the proposed modifications from
     * students.
     *
     * @param companyId the ID of the company
     * @return a List of all the modifications that were proposed by the students to that company.
     */
    @GetMapping("/{companyId}/modification/proposed")
    @ResponseBody
    public ModificationResponse getProposedModificationsByCompanyId(
        @PathVariable String companyId) {
        return companyService.getModificationsToRespond(companyId);
    }

    /**
     * Sends a request to the Contract Microservice to get the all the proposals of modifications
     * in which that company has been involved.
     *
     * @param companyId the ID of the company
     * @return a List of all the modifications
     */
    @GetMapping("/{companyId}/modification/all")
    @ResponseBody
    public ModificationResponse getAllInvolvedInModifications(
        @PathVariable String companyId) {
        return companyService.getAllInvolvedInModifications(companyId);
    }

    /**
     * Accepts the changes requested by a student for a job service.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the updated CompanyRequest.
     */
    @PostMapping("/{companyId}/request/acceptChanges/{modificationId}")
    @ResponseBody
    public CompanyRequest acceptSuggestedChanges(@PathVariable String companyId,
                                                 @PathVariable Long modificationId) {
        return companyService.acceptSuggestedChanges(companyId, modificationId);
    }

    /**
     * Rejects the changes requested by a student for a job service.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the original CompanyRequest.
     */
    @PostMapping("/{companyId}/request/rejectChanges/{modificationId}")
    @ResponseBody
    public CompanyRequest rejectSuggestedChanges(@PathVariable String companyId,
                                                 @PathVariable Long modificationId) {
        return companyService.rejectSuggestedChanges(companyId, modificationId);
    }

    /**
     * Sends a request to the Request microservice to
     * return a list of all modifications proposed by students.
     *
     * @param companyId the ID of the company.
     * @return a list of all modifications.
     */
    @GetMapping("/{companyId}/request/modifications/all")
    @ResponseBody
    public List<CompanyRequestModification>
        getAllJobModifications(@PathVariable String companyId) {
        return companyService.getAllJobModifications(companyId);
    }

    /**
     * Sends a company request to the service to save a request
     * that targets a specific student.
     * Returns the request that is saved.
     *
     * @param companyId the company that sends the request
     * @param studentId the student that is targeted by the request
     * @param companyRequest the request that targets the student
     * @return the request that is saved.
     */
    @PostMapping("/{companyId}/request/targeted/{studentId}")
    @ResponseBody
    public CompanyRequest postTargetedCompanyRequest(
            @PathVariable String companyId,
            @PathVariable String studentId,
            @RequestBody CompanyRequest companyRequest) {
        log.info("The Company " + companyId + " sends a targeted request to target " + studentId);
        return companyService.postTargetedCompanyRequest(companyRequest, studentId);
    }

    /**
     * Sends a GET request to the Request Service to get all students
     * that are available for that amount of hours.
     *
     * @param companyId the Id of the company
     * @param filterTag the filter tag in order to determine the specific filter to apply
     * @return returns a List of StudentRequests that are available
     */
    @PostMapping("/{companyId}/search")
    @ResponseBody
    public List<StudentRequest> searchAvailableStudents(@PathVariable String companyId,
                                                        @RequestBody FilterTag filterTag) {
        log.info("Company" + companyId + "is searching for students available between "
                +  filterTag.getStartOfTheInterval()
                + " and " + filterTag.getEndOfTheInterval() + " hours");
        return companyService.searchAvailableStudents(filterTag);
    }
}
