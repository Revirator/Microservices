package nl.tudelft.sem.student.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import nl.tudelft.sem.student.valueobjects.CompanyRequest;
import nl.tudelft.sem.student.valueobjects.CompanyRequestModification;
import nl.tudelft.sem.student.valueobjects.CompanyRequestResponse;
import nl.tudelft.sem.student.valueobjects.FilterTag;
import nl.tudelft.sem.student.valueobjects.StudentRequest;
import nl.tudelft.sem.student.valueobjects.StudentRequestChange;
import nl.tudelft.sem.student.valueobjects.StudentRequestResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentServiceRequest extends StudentService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new GsonBuilder().create();
    private final String requestUrl = "http://REQUEST-SERVICE/request/student/";

    public StudentServiceRequest(RestTemplate restTemplate, StudentRepository studentRepository) {
        super(restTemplate, studentRepository);
    }

    /**
     * Receives a request from the controller and forwards it to the
     * RequestService microservice.
     *
     * @return a List of all the requests that are made by companies
     */
    public List<CompanyRequest> getAllRequests() {
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://REQUEST-SERVICE/request/company", List.class);
        assert response != null;
        return responseToList(response, CompanyRequest.class);
    }

    /**
     * Receives a request from the controller and forwards it to the
     * RequestService microservice.
     *
     * @return a List of all the requests that are made by a company
     */
    public List<CompanyRequest> getRequestByCompanyId(String companyId) {
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://REQUEST-SERVICE/request/company/" + companyId,
                List.class);
        assert response != null;
        return responseToList(response, CompanyRequest.class);
    }

    /**
     * Receives a request from the controller and forwards it to the
     * RequestService microservice.
     *
     * @param filterTag the filter tag in order to determine the specific filter to apply
     * @return a List of all the company requests that correspond to that specific filter
     */
    public List<CompanyRequest> applyFilter(FilterTag filterTag) {
        String requestJsonObject = gson.toJson(filterTag);
        HttpEntity<String> request = requestCreator(requestJsonObject);
        List<LinkedHashMap> response =
            restTemplate.postForObject("http://REQUEST" + "-SERVICE/request/company/filter/",
                request, List.class);
        return responseToList(response, CompanyRequest.class);

    }



    /**
     * Gets the most recent Student Request via serviceId.
     *
     * @param serviceId The ID of the service/request.
     * @return the most recent StudentRequestResponse.
     */
    public StudentRequestResponse getStudentRequest(Long serviceId) {
        StudentRequest request = restTemplate.getForObject(
            "http://REQUEST-SERVICE/request" + "/student/serviceId/" + serviceId,
            StudentRequest.class);
        Student student = studentRepository.findStudentByNetId(request.getStudentId());
        StudentRequestResponse res = new StudentRequestResponse();
        res.setStudent(student);
        res.setStudentRequest(request);
        return res;
    }

    /**
     * Gets the most recent Company Request via serviceId.
     *
     * @param serviceId The ID of the service/request.
     * @return the most recent CompanyRequestResponse.
     */
    public CompanyRequestResponse getCompanyRequest(Long serviceId) {
        CompanyRequest request = restTemplate.getForObject(
            "http://REQUEST-SERVICE/request" + "/company/serviceId/" + serviceId,
            CompanyRequest.class);
        Student student = studentRepository.findStudentByNetId(request.getStudentId());
        CompanyRequestResponse res = new CompanyRequestResponse();
        res.setStudent(student);
        res.setCompanyRequest(request);
        return res;
    }

    /**
     * Accepts the StudentRequest the student him/herself put out, assuming that the company has
     * also accepted as well, generating a contract.
     *
     * @param studentId The ID of the student
     * @param serviceId The ID of the service
     * @return A StudentRequestResponse
     */
    public StudentRequestResponse acceptByStudent(String studentId, Long serviceId) {
        HttpEntity<String> request = requestCreator("{}");
        restTemplate.postForObject(
            requestUrl + "acceptOwn/" + studentId + "/" + serviceId,
            request, StudentRequest.class);
        return getStudentRequest(serviceId);
    }

    /**
     * Accepts the request a company put up as a student.
     *
     * @param studentId The ID of the student.
     * @param serviceId The ID of the service.
     * @return The CompanyRequestResponse.
     */
    public CompanyRequestResponse acceptCompanyRequest(String studentId, Long serviceId) {
        HttpEntity<String> request = requestCreator("{}");
        restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/company/" + studentId + "/" + serviceId, request,
            CompanyRequest.class);
        return getCompanyRequest(serviceId);
    }

    /**
     * Rejects the company request.
     *
     * @param studentId The ID of the student.
     * @param serviceId The ID of the service/request
     * @return The StudentRequestResponse.
     */
    public StudentRequestResponse rejectCompanyRequest(String studentId, Long serviceId) {
        HttpEntity<String> request = requestCreator("{}");
        restTemplate.postForObject("http://REQUEST-SERVICE/student/reject/" + serviceId, request,
            StudentRequest.class);
        return getStudentRequest(serviceId);
    }

    /**
     * Send the modification request to the Request microservice.
     *
     * @param studentId the ID of the student.
     * @param serviceId the ID of the service.
     * @param companyRequestModification the changes that the student wants to make.
     * @return the CompanyRequestModification.
     */
    public CompanyRequestModification requestChangesToCompanyRequest(
            String studentId, Long serviceId,
            CompanyRequestModification companyRequestModification) {
        companyRequestModification.setStudentId(studentId);
        companyRequestModification.setServiceId(serviceId);
        companyRequestModification.setAcceptedByCompany(false);
        String modificationJsonObject = gson.toJson(companyRequestModification);
        HttpEntity<String> request = requestCreator(modificationJsonObject);
        return restTemplate.postForObject("http://REQUEST-SERVICE/modification/" + serviceId, request, CompanyRequestModification.class);
    }

    /**
     * Returns a StudentRequest via studentID.
     *
     * @param studentId The ID of the student
     * @return the StudentRequest with the studentId
     */
    public StudentRequest getStudentRequestByStudentId(String studentId) {
        return restTemplate.getForObject(requestUrl + studentId,
            StudentRequest.class);
    }

    /**
     * Creates a service request by the student.
     *
     * @param netId       - the Net ID of the student making the request.
     * @param requestData - the data included in the request.
     * @return the newly-generated service request.
     */
    public StudentRequest postStudentRequest(String netId, StudentRequest requestData) {
        requestData.setStudentId(netId);
        // If the student already had an existing service request, we delete the old one.
        StudentRequest oldRequest =
            restTemplate.getForObject(requestUrl + netId,
                StudentRequest.class);
        if (oldRequest != null) {
            HttpEntity<String> request = requestCreator("{}");
            restTemplate.delete(requestUrl + netId, request,
                StudentRequest.class);
        }
        // Create the new service request
        String requestJsonObject = gson.toJson(requestData);
        HttpEntity<String> request = requestCreator(requestJsonObject);
        return restTemplate.postForObject(requestUrl, request,
            StudentRequest.class);
    }

    /**
     * Deletes a student's service request.
     *
     * @param netId - the net id of the student whose request to delete.
     * @return the deleted request.
     */
    public StudentRequest deleteStudentRequest(String netId) {
        StudentRequest request =
            restTemplate.getForObject(requestUrl + netId,
                StudentRequest.class);
        if (request == null) {
            return null;
        }
        restTemplate.delete(requestUrl + netId,
            StudentRequest.class);
        return request;
    }

    /**
     * Updates the student's request parameters without changing the student or service id.
     *
     * @param netId       - the netId of the student whose request to update.
     * @param requestData - the wanted new changes to the request data.
     * @return the updated request.
     */
    public StudentRequest updateStudentRequest(String netId, StudentRequestChange requestData) {
        String requestJsonObject = gson.toJson(requestData);
        HttpEntity<String> request = requestCreator(requestJsonObject);
        restTemplate.put(requestUrl + netId, request,
            StudentRequest.class);
        return restTemplate.getForObject(requestUrl + netId,
            StudentRequest.class);
    }

    /**
     * Receives a request from the controller to extract all targeted
     * requests from the Request microservice.
     *
     * @param studentId the student to get the targeted requests for.
     * @return a list of targeted requests
     */
    public List<CompanyRequest> getTargetedRequest(String studentId) {
        List<LinkedHashMap> response = restTemplate
            .getForObject("http://REQUEST-SERVICE/request/student/targeted/" + studentId, List.class);
        return responseToList(response, CompanyRequest.class);
    }

}
