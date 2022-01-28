package nl.tudelft.sem.company.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.repository.CompanyRepository;
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
import nl.tudelft.sem.company.valueobjects.Student;
import nl.tudelft.sem.company.valueobjects.StudentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Service
@Slf4j
public class CompanyService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new GsonBuilder().create();
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @param company the company Object that needs to be saved
     * @return The company that is saved
     */
    public Company saveCompany(Company company) {
        log.info("Inside saveCompany of CompanyService");
        return companyRepository.save(company);
    }

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @param username the username of the user
     * @return The company that matches the username (null if username doesn't exist)
     */
    public Company findCompanyByUsername(String username) {
        log.info("Inside findCompanyByName of CompanyService");
        return companyRepository.findCompanyByUsername(username);
    }

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @return a List of all the companies in the database
     */
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    /**
     * Receives a request from the controller and forwards it to
     * the Feedback and Student microservices.
     *
     * @param studentId the ID for the student for which we want to get the feedbacks
     * @return a List of all the feedbacks the student has received including the new one
     */
    public FeedbackResponse getFeedbackForStudent(String studentId) {
        log.info("Inside getFeedbackForStudent of CompanyService");
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + studentId, List.class);
        double averageRating = 0;
        List<Feedback> feedbacks = parseList(response, Feedback.class);
        for (Feedback f : feedbacks) {
            averageRating += f.getStarRating();
        }
        averageRating /= feedbacks.size();
        Student student =
            restTemplate.getForObject("http://STUDENT-SERVICE/student/" + studentId + "/",
                Student.class);
        student.setAverageRating(Math.max(0, averageRating));
        FeedbackResponse vo = new FeedbackResponse();
        vo.setStudent(student);
        vo.setFeedbacks(feedbacks);
        return vo;
    }

    /**
     * Receives a request from the controller and forwards it to the Feedback microservice.
     * Also invokes the getFeedbackForStudent to return a List of all feedbacks.
     *
     * @param studentId the ID for the student for which we want to post the feedback
     * @param feedback  new feedback to be posted for that student
     * @return a List of all the feedbacks the student has received including the new one
     */
    public FeedbackResponse postFeedbackForStudent(String studentId, Feedback feedback) {
        log.info("Inside postFeedbackForStudent of CompanyService");
        HttpEntity<String> request = createRequest(gson.toJson(feedback));
        restTemplate.postForObject("http://FEEDBACK-SERVICE/feedback/" + studentId, request,
            Feedback.class);
        return getFeedbackForStudent(studentId);
    }

    /**
     * Receives a request from the controller and forwards it to the
     * RequestService microservice.
     *
     * @return a List of all the requests that are made by students
     */
    public List<StudentRequest> getAllRequests() {
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://REQUEST-SERVICE/request/student", List.class);
        return parseList(response, StudentRequest.class);
    }

    /**
     * Gets all contracts by companyId by forwarding the request to the Contract
     * microservice.
     *
     * @param companyId The username of the company
     * @return a List of all the contracts of the company.
     */
    public ContractResponse getContractsByCompanyId(String companyId) {
        log.info("Inside getContractsByCompanyId of CompanyService");
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://CONTRACT-SERVICE/contracts/company/" + companyId,
                List.class);
        return new ContractResponse(parseList(response, Contract.class));
    }

    /**
     * Allows company to accept its own request, assuming that a student has accepted it as well.
     *
     * @param companyId The ID of the company.
     * @param serviceId The ID of the service/request.
     * @param studentId The ID of the student to be chosen from the candidates.
     * @return The accepted CompanyRequest.
     */
    public CompanyRequest acceptByCompany(String companyId, Long serviceId, String studentId) {
        HttpEntity<String> request = createRequest("{}");
        restTemplate.postForObject(
                "http://REQUEST-SERVICE/request/company/acceptOwn/" + companyId + "/" + serviceId
                        + "/" + studentId,
            request, CompanyRequest.class);
        return getCompanyRequest(serviceId);
    }

    /**
     * Gets the most recently updated Company Request via serviceId.
     *
     * @param serviceId The ID of the service/request
     * @return the most recent CompanyRequest.
     */
    public CompanyRequest getCompanyRequest(Long serviceId) {
        CompanyRequest request = restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/company/service/" + serviceId,
            CompanyRequest.class);
        return request;
    }

    /**
     * Accepts the student request as a company.
     *
     * @param companyId The ID of the company
     * @param serviceId The ID of the service/request
     * @return The StudentRequest with the companyId field filled with our companyId.
     */
    public StudentRequest acceptStudentRequest(String companyId, Long serviceId) {
        HttpEntity<String> request = createRequest("{}");
        restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/student/accept/" + companyId + "/" + serviceId, request,
            StudentRequest.class);
        //Returns the updated version of our student request.
        return getStudentRequest(serviceId);
    }

    /**
     * Gets the most recently updated Student Request via serviceId.
     *
     * @param serviceId The ID of the service/request
     * @return the most recent StudentRequest
     */
    public StudentRequest getStudentRequest(Long serviceId) {
        StudentRequest request = restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/student/serviceId/" + serviceId,
            StudentRequest.class);
        return request;
    }

    /**
     * Receives a request from the controller and forwards it to the
     * Request microservice. Also invokes the getAllStudentRequests to return a List
     * of all StudentRequests.
     * The method searchExpertiseStudents expects a expertise (String) and will
     * return a List of students which have that expertise listed in their expertise
     * List in the StudentRequest.
     *
     * @param expertise expert skill or knowledge on which a company wants to filter students.
     * @return a List of type String with all the students that have the defined expertise
     */
    public List<StudentRequest> searchExpertiseStudents(String expertise) {
        log.info("Inside searchExpertiseStudents of CompanyService");
        List<StudentRequest> response =
            restTemplate.getForObject("http://REQUEST-SERVICE/request/company/search/" + expertise,
                List.class);
        return response;
    }

    /**
     * Deletes all the company job requests.
     *
     * @param companyId the name of the company from whom all the requests need to be deleted
     * @return the deleted requests
     */
    public List<CompanyRequest> deleteAllCompanyRequests(String companyId) {
        List<LinkedHashMap> response = restTemplate
                .getForObject("http://REQUEST-SERVICE/request/company/" + companyId, List.class);
        List<CompanyRequest> requests = parseList(response, CompanyRequest.class);
        restTemplate.delete(
                "http://REQUEST-SERVICE/request/company/" + companyId, List.class);
        return requests;
    }

    /**
     * Delete certain company's job requests.
     *
     * @param companyId - the net id of the company whose request to delete.
     * @param serviceId - the Id of the service that will be deleted
     * @return the deleted request.
     */
    public CompanyRequest deleteCompanyRequest(String companyId, Long serviceId) {
        String url = "http://REQUEST-SERVICE/request/company/service/";
        CompanyRequest request = restTemplate.getForObject(
                url + serviceId, CompanyRequest.class);
        if (companyId.equals(request.getCompanyId())) {
            restTemplate.delete(url + serviceId, CompanyRequest.class);
            return request;
        }
        return null;
    }

    /**
     * Updates the company's request parameters without changing the company or service id.
     *
     * @param companyId - the Id of the company whose request to update.
     * @param serviceId - the id of the service to update.
     * @param requested - the wanted new changes to the request data.
     * @return the updated request
     */
    public CompanyRequest updateCompanyRequest(String companyId, Long serviceId,
                                               CompanyRequestChange requested) {
        HttpEntity<String> request = createRequest(gson.toJson(requested));
        restTemplate.put("http://REQUEST-SERVICE/request/company/service/" + serviceId, request,
                CompanyRequest.class);
        return restTemplate.getForObject("http://REQUEST-SERVICE/request/company/service/" + serviceId,
                CompanyRequest.class);
    }

    /** Sends request to the Contract microservice to propose a contractModification as a company.
    *
    * @param contractModification the modification to be proposed
    * @return the modification
    */
    public ContractModification proposeModification(ContractModification contractModification) {
        log.info("Proposed modification for a contract");
        contractModification.setAcceptedByCompany(true);
        contractModification.setAcceptedByStudent(false);
        contractModification.setFinished(false);
        return makeModificationRequest(contractModification);
    }

    /** Sends request to the Contract microservice to propose a contractModification as a company.
     *
     * @param contractModification the modification to be proposed
     * @return return the modificationRequest
     */
    public ContractModification makeModificationRequest(ContractModification contractModification) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String modificationJsonObject = gson.toJson(contractModification);
        HttpEntity<String> request = new HttpEntity<>(modificationJsonObject, headers);
        return restTemplate.postForObject("http://CONTRACT-SERVICE/modification/", request,
                ContractModification.class);
    }

    /**
     * Sends request to the Contract microservice to accept a contractModification as a company.
     *
     * @param companyId      The ID of the company
     * @param modificationId The ID of the modification.
     * @return the modified contract
     */
    public Contract acceptModification(String companyId, Long modificationId) {
        HttpEntity<String> request = createRequest("{}");
        return restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/company/" + companyId + "/accepts/"
                + modificationId, request, Contract.class);
    }

    /**
     * Sends request to the Contract microservice to decline a contractModification as a company.
     *
     * @param companyId      The ID of the company
     * @param modificationId The ID of the modification.
     * @return the decline modification
     */
    public ContractModification declineModification(String companyId, Long modificationId) {
        HttpEntity<String> request = createRequest("{}");
        return restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/company/" + companyId + "/" + "declines/"
                + modificationId, request, ContractModification.class);
    }

    /**
     * Sends a request to the Contract Microservice to get the proposed modifications from
     * students.
     *
     * @param companyId the ID of the company
     * @return a List of all the modifications that were proposed by the students to that company.
     */
    public ModificationResponse getModificationsToRespond(String companyId) {
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://CONTRACT-SERVICE/modification/company/" + companyId,
                List.class);
        List<ContractModification> contractModifications = parseList(response,
                ContractModification.class);
        Company company = companyRepository.findCompanyByUsername(companyId);
        ModificationResponse modificationResponse = new ModificationResponse();
        modificationResponse.setCompany(company);
        modificationResponse.setContractModifications(contractModifications);
        return modificationResponse;
    }

    /**
     * Sends a request to the Contract Microservice to get the all the proposals of modifications
     * in which that company has been involved.
     *
     * @param companyId the ID of the company
     * @return a List of all the modifications
     */
    public ModificationResponse getAllInvolvedInModifications(String companyId) {
        List<LinkedHashMap> response = restTemplate.getForObject(
            "http://CONTRACT-SERVICE/modification/all/company/" + companyId, List.class);
        List<ContractModification> contractModifications = parseList(response,
                ContractModification.class);
        Company company = companyRepository.findCompanyByUsername(companyId);
        ModificationResponse modificationResponse = new ModificationResponse();
        modificationResponse.setCompany(company);
        modificationResponse.setContractModifications(contractModifications);
        return modificationResponse;
    }

    /**
     * Sends a request to the Request Micro-Service to save a targeted request.
     * Returns the request that is saved.
     *
     * @param companyRequest the request that targets the student.
     * @param studentId the student that is targeted.
     * @return the request that is saved.
     */
    public CompanyRequest postTargetedCompanyRequest(
            CompanyRequest companyRequest, String studentId) {
        HttpEntity<String> request = createRequest(gson.toJson(companyRequest));
        restTemplate.postForObject("http://REQUEST-SERVICE/request/company/targeted/" + studentId, request,
                CompanyRequest.class);
        return getCompanyRequest(companyRequest.getServiceId());
    }

    /**
     * Receives a request from the controller and forwards it to the
     * RequestService microservice.
     *
     * @param filterTag the filter tag in order to determine the specific filter to apply
     * @return a List of all the student requests that correspond to that specific filter
     */
    public List<StudentRequest> searchAvailableStudents(FilterTag filterTag) {
        HttpEntity<String> request = createRequest(gson.toJson(filterTag));
        List<LinkedHashMap> response =
                restTemplate.postForObject("http://REQUEST-SERVICE/request/student/filter/",
                        request, List.class);
        return parseList(response, StudentRequest.class);
    }

    /**
     * Sends a request to the Request microservice to accept
     * the changes and update the job service.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the updated CompanyRequest.
     */
    public CompanyRequest acceptSuggestedChanges(String companyId, Long modificationId) {
        HttpEntity<String> request = createRequest("{}");
        return restTemplate.postForObject(
                "http://REQUEST-SERVICE/modification/company/" + companyId + "/accepts/" + modificationId,
                request, CompanyRequest.class);
    }

    /**
     * Sends a request to the Request microservice to reject the
     * changes and delete the modification request.
     *
     * @param companyId the ID  of tre company.
     * @param modificationId the ID of the modification.
     * @return the original CompanyRequest.
     */
    public CompanyRequest rejectSuggestedChanges(String companyId, Long modificationId) {
        HttpEntity<String> request = createRequest("{}");
        return restTemplate.postForObject(
                "http://REQUEST-SERVICE/modification/company/" + companyId + "/rejects/" + modificationId,
                request, CompanyRequest.class);
    }

    /**
     * Sends a request to the Request microservice to
     * return a list of all modifications proposed by students.
     *
     * @param companyId the ID of the company.
     * @return a list of all modifications.
     */
    public List<CompanyRequestModification> getAllJobModifications(String companyId) {
        List<LinkedHashMap> response = restTemplate
                .getForObject("http://REQUEST-MICROSERVICE/"
                        + "modification/company/" +  companyId + "/all", List.class);
        return parseList(response, CompanyRequestModification.class);
    }

    /**
     * Sends a request to the Request Microservice to post a new CompanyRequest for the company.
     *
     * @param companyId The ID of the company.
     * @param companyRequest The request the company wants to put up
     * @return The posted CompanyRequest
     */
    public CompanyRequest postCompanyRequest(String companyId, CompanyRequest companyRequest) {
        log.info("Inside postCompanyRequest of CompanyService");
        companyRequest.setCompanyId(companyId);
        HttpEntity<String> request = createRequest(gson.toJson(companyRequest));
        return restTemplate.postForObject("http://REQUEST-SERVICE/request/company", request,
            CompanyRequest.class);
    }

    private HttpEntity<String> createRequest(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    private <T> List<T> parseList(List<LinkedHashMap> map, Class<T> klass) {
        List<T> list = new ArrayList<>();
        for (LinkedHashMap o : map) {
            list.add(mapper.convertValue(o, klass));
        }
        return list;
    }
}
