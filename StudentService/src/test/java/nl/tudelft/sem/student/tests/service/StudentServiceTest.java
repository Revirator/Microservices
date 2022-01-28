package nl.tudelft.sem.student.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class StudentServiceTest {

    private final Gson gson = new GsonBuilder().create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String mherreboutNetId = "teambeurnaut";
    private final String companyUsername = "amazon94";
    private final Student s1 = new Student(mherreboutNetId, "Marijn", 0, true, 3.0);
    private final Company company = new Company(companyUsername, "Amazon", 2.5);
    private final Feedback feedback = new Feedback(2L, 4, "Great company!", companyUsername);
    private final List<Feedback> feedbacks =
        List.of(feedback, new Feedback(1L, 1, "Jeff exploits his workers :(", companyUsername));
    private final CompanyRequest request =
        new CompanyRequest(1L, 6, 16, 10, companyUsername, List.of(
            "requirement1", "req2"),
            false,
          "");
    private final List<CompanyRequest> companyRequests = List.of((CompanyRequest) request);
    private final ContractModification postContractModification =
        new ContractModification("MODIFICATION", 1L, "student1", "company1", 15, 60, 5);
    private final ContractModification companyProposedModification =
        new ContractModification(2L, "EXTENSION", 2L, "student2", "company2", 15, 60, 5, true,
            false, false);
    private final ContractModification declinedCompanyModification =
        new ContractModification(2L, "EXTENSION", 2L, "student2", "company2", 15, 60, 5, true,
            false, true);
    private final List<ContractModification> companyProposedModifications =
        List.of(companyProposedModification);
    private final List<ContractModification> studentInvolvedInProposals =
        List.of(companyProposedModification, declinedCompanyModification);
    private final Student studentModif = new Student("student2", "Student too", 12, false, 3.0);
    private final Contract resultedContract =
        new Contract(2L, 15, 60, 5, "student2", "company2", false);
    String requestServiceUrl = "http://REQUEST-SERVICE/request";
    String studentServiceUrl = "http://STUDENT-SERVICE/student/";
    String studentId = "mthijs";
    private final Student s2 = new Student(studentId, "Matthijs", 3, false, 4.0);
    private final Contract c1 = new Contract(1L, 16, 48, 13.5, studentId, "Amazon", false);
    private final Contract c2 = new Contract(2L, 2, 10, 13.5, studentId, "Apple", false);
    private final List<Contract> contracts = List.of(c1, c2);
    private final StudentRequest sr1 =
        new StudentRequest(1L, 16, 48, 15, studentId, null, "", false);
    private final CompanyRequest cr1 =
        new CompanyRequest(1L, 16, 48, 15, "Microsoft", null, false, studentId);
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private StudentRepository studentRepository;

    private final String requestUrl = "http://REQUEST-SERVICE/request/student/";

    @InjectMocks
    private StudentService studentService;
    @InjectMocks
    private StudentServiceContract studentServiceContract;
    @InjectMocks
    private StudentServiceFeedback studentServiceFeedback;
    @InjectMocks
    private StudentServiceRequest studentServiceRequest;

    /**
     * Setup method for testing.
     */
    @BeforeEach
    public void setup() {
        studentService = new StudentService(restTemplate, studentRepository);
        studentServiceContract = new StudentServiceContract(restTemplate, studentRepository);
        studentServiceFeedback = new StudentServiceFeedback(restTemplate, studentRepository);
        studentServiceRequest = new StudentServiceRequest(restTemplate, studentRepository);
    }

    @Test
    public void testGetAll() {
        given(studentRepository.findAll()).willReturn(List.of(s1, s2));
        List<Student> students = studentService.getAll();
        assertThat(students).hasSameElementsAs(List.of(s1, s2));
        verify(studentRepository).findAll();
    }

    @Test
    public void testFindByNetId() {
        given(studentRepository.findStudentByNetId(mherreboutNetId)).willReturn(s1);
        Student student = studentService.findStudentByNetId(mherreboutNetId);
        assertEquals(student, s1);
        verify(studentRepository).findStudentByNetId(mherreboutNetId);
    }

    @Test
    public void testSaveStudent() {
        given(studentRepository.save(new Student(mherreboutNetId, s1.getName()))).willReturn(s1);
        Student student = studentService.saveStudent(mherreboutNetId, s1);
        assertEquals(student, s1);
        verify(studentRepository).save(new Student(mherreboutNetId, s1.getName()));
    }

    @Test
    public void testGetFeedbackForCompany() {
        List<LinkedHashMap> feedbackResponse = new ArrayList<>();
        for (Feedback f : feedbacks) {
            feedbackResponse.add(mapper.convertValue(f, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + companyUsername,
            List.class)).thenReturn(feedbackResponse);
        when(restTemplate.getForObject("http://COMPANY-SERVICE/company/" + companyUsername + "/",
            Company.class)).thenReturn(company);
        FeedbackResponse response = studentServiceFeedback.getFeedbackForCompany(companyUsername);
        assertEquals(response, new FeedbackResponse(company, feedbacks));
    }

    @Test
    public void testPostFeedbackForCompany() {
        List<LinkedHashMap> feedbackResponse = new ArrayList<>();
        for (Feedback f : feedbacks) {
            feedbackResponse.add(mapper.convertValue(f, LinkedHashMap.class));
        }
        Feedback postFeedback = new Feedback(5, "Test feedback.");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String feedbackJsonObject = gson.toJson(postFeedback);
        HttpEntity<String> request = new HttpEntity<>(feedbackJsonObject, headers);
        when(restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + companyUsername,
            List.class)).thenReturn(feedbackResponse);
        when(restTemplate.postForObject("http://FEEDBACK-SERVICE/feedback/" + companyUsername,
            request, Feedback.class)).thenReturn(feedback);
        when(restTemplate.getForObject("http://COMPANY-SERVICE/company/" + companyUsername + "/",
            Company.class)).thenReturn(company);
        FeedbackResponse response =
            studentServiceFeedback.postFeedbackForCompany(companyUsername, postFeedback);
        assertThat(response.getFeedbacks()).contains(feedback);
        assertEquals(response, new FeedbackResponse(company, feedbacks));
    }

    @Test
    public void testGetAllRequests() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (CompanyRequest c : companyRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/company",
            List.class)).thenReturn(requests);

        List<CompanyRequest> response = studentServiceRequest.getAllRequests();
        assertEquals(companyRequests, response);
    }

    @Test
    public void testGetRequestsByCompanyId() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (CompanyRequest c : companyRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/company/" + companyUsername,
            List.class)).thenReturn(requests);

        List<CompanyRequest> response = studentServiceRequest
                .getRequestByCompanyId(companyUsername);
        assertEquals(companyUsername, response.get(0).getCompanyId());
    }

    @Test
    public void testGetStudentRequestByStudentId() {
        String studentId = "mherrebout";
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/student/" + studentId,
            StudentRequest.class)).thenReturn(sr1);
        StudentRequest result = studentServiceRequest.getStudentRequestByStudentId(studentId);
        assertEquals(result, sr1);
    }

    @Test
    public void testGetRequestsByHoursPerWeek() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (CompanyRequest c : companyRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }

        FilterTag filterTag = new FilterTag("hoursPerWeek", 5, 11);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(filterTag);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);

        when(restTemplate.postForObject("http://REQUEST-SERVICE/request/company/filter/", request,
            List.class)).thenReturn(requests);

        List<CompanyRequest> response = studentServiceRequest.applyFilter(filterTag);
        assertTrue(
            response.get(0).getHoursPerWeek() >= 5 && response.get(0).getHoursPerWeek() <= 11);
    }

    @Test
    public void testGetRequestsByTotalHours() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (CompanyRequest c : companyRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }

        FilterTag filterTag = new FilterTag("totalHours", 5, 11);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(filterTag);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);

        when(restTemplate.postForObject("http://REQUEST-SERVICE/request/company/filter/", request,
            List.class)).thenReturn(requests);

        List<CompanyRequest> response = studentServiceRequest.applyFilter(filterTag);
        assertTrue(response.get(0).getTotalHours() >= 15 && response.get(0).getTotalHours() <= 21);
    }

    @Test
    public void testGetRequestsBySalary() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (CompanyRequest c : companyRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }

        FilterTag filterTag = new FilterTag("salary", 5, 11);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(filterTag);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);

        when(restTemplate.postForObject("http://REQUEST-SERVICE/request/company/filter/", request,
            List.class)).thenReturn(requests);

        List<CompanyRequest> response = studentServiceRequest.applyFilter(filterTag);
        assertTrue(
            response.get(0).getSalaryPerHour() >= 5 && response.get(0).getSalaryPerHour() <= 11);
    }

    @Test
    public void testPostStudentRequest() {
        StudentRequest studentRequest =
            new StudentRequest(1L, 4, 32, 400, mherreboutNetId, List.of("IT", "Support"), "",
                false);
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/student/" + mherreboutNetId,
            StudentRequest.class)).thenReturn(null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(studentRequest);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);
        when(restTemplate.postForObject(requestUrl, request,
            StudentRequest.class)).thenReturn(studentRequest);
        StudentRequest result =
            studentServiceRequest.postStudentRequest(mherreboutNetId, studentRequest);
        assertEquals(studentRequest, result);
    }

    @Test
    public void testPostStudentRequestWithOldRequest() {
        StudentRequest studentRequest =
            new StudentRequest(1L, 4, 32, 400, mherreboutNetId, List.of("IT", "Support"), "",
                false);
        StudentRequest oldRequest =
            new StudentRequest(3L, 10, 40, 500, "revirator", List.of("Cracked"), "", false);
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/student/" + mherreboutNetId,
            StudentRequest.class)).thenReturn(oldRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(studentRequest);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);
        when(restTemplate.postForObject(requestUrl, request,
            StudentRequest.class)).thenReturn(studentRequest);
        StudentRequest result = studentServiceRequest.postStudentRequest(mherreboutNetId,
            studentRequest);
        assertEquals(result, studentRequest);
    }

    @Test
    public void testUpdateStudentRequest() {
        String studentId = "mherrebout";
        StudentRequest updatedRequest =
            new StudentRequest(1L, 32, 160, 20, studentId, List.of("IT"), null, false);
        StudentRequestChange changes = new StudentRequestChange();
        changes.setSalaryPerHour(20);
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/student/" + studentId,
            StudentRequest.class)).thenReturn(updatedRequest);
        StudentRequest result = studentServiceRequest.updateStudentRequest(studentId, changes);
        assertEquals(result, updatedRequest);
    }

    @Test
    public void testGetStudentRequest() {
        Long serviceId = 1L;
        when(restTemplate.getForObject(requestServiceUrl + "/student/serviceId/" + serviceId,
            StudentRequest.class)).thenReturn(sr1);
        when(restTemplate.getForObject(studentServiceUrl + studentId, Student.class)).thenReturn(
            s2);
        StudentRequestResponse result = studentServiceRequest.getStudentRequest(serviceId);
        assertEquals(result.getStudentRequest(), sr1);
    }

    @Test
    public void testGetCompanyRequest() {
        Long serviceId = 1L;
        when(restTemplate.getForObject(requestServiceUrl + "/company/serviceId/" + serviceId,
            CompanyRequest.class)).thenReturn(cr1);
        when(restTemplate.getForObject(studentServiceUrl + studentId, Student.class)).thenReturn(
            s2);
        CompanyRequestResponse result = studentServiceRequest.getCompanyRequest(serviceId);
        assertEquals(result.getCompanyRequest(), cr1);
    }

    @Test
    public void acceptByStudent() {
        Long serviceId = 1L;
        StudentRequest sr1True = new StudentRequest(1L, 16, 48, 15, "mthijs", null, "Amazon", true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/student/acceptOwn/" + studentId + "/" + serviceId,
            request, StudentRequest.class)).thenReturn(sr1True);
        when(restTemplate.getForObject(requestServiceUrl + "/student/serviceId/" + serviceId,
            StudentRequest.class)).thenReturn(sr1True);
        when(restTemplate.getForObject(studentServiceUrl + studentId, Student.class)).thenReturn(
            s2);
        StudentRequestResponse result = studentServiceRequest.acceptByStudent(studentId, serviceId);
        assertThat(result.getStudentRequest()).isEqualTo(sr1True);
    }

    @Test
    public void acceptCompanyRequest() {
        Long serviceId = 1L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        final CompanyRequest cr1 =
            new CompanyRequest(1L, 16, 48, 15,
                    "Microsoft", null, false, "mthijs");
        when(restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/company/" + studentId + "/" + serviceId, request,
            CompanyRequest.class)).thenReturn(cr1);
        when(restTemplate.getForObject(requestServiceUrl + "/company/serviceId/" + serviceId,
            CompanyRequest.class)).thenReturn(cr1);
        when(restTemplate.getForObject(studentServiceUrl + studentId, Student.class)).thenReturn(
            s2);
        CompanyRequestResponse result = studentServiceRequest
                .acceptCompanyRequest(studentId, serviceId);
        assertThat(result.getCompanyRequest()).isEqualTo(cr1);
    }

    @Test
    public void rejectCompanyRequest() {
        //TODO: Next issue
    }

    @Test
    public void testProposeModification() {
        ContractModification proposedModification =
            new ContractModification("MODIFICATION", 1L, "student1", "company1", 15, 60, 5, false,
                true, false);
        ContractModification resultedModification =
            new ContractModification(1L, "MODIFICATION", 1L, "student1", "company1", 15, 60, 5,
                false, true, false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String modificationJsonObject = gson.toJson(proposedModification);
        HttpEntity<String> request = new HttpEntity<>(modificationJsonObject, headers);

        when(restTemplate.postForObject("http://CONTRACT-SERVICE/modification/", request,
            ContractModification.class)).thenReturn(resultedModification);
        ContractModification response =
            studentServiceContract.proposeModification(postContractModification);
        assertEquals(response,
            new ContractModification(1L, "MODIFICATION", 1L, "student1", "company1", 15, 60, 5,
                false, true, false));
    }

    @Test
    public void testGetModificationsToRespond() {
        List<LinkedHashMap> modificationsResponse = new ArrayList<>();
        for (ContractModification contractModification : companyProposedModifications) {
            modificationsResponse.add(
                mapper.convertValue(contractModification, LinkedHashMap.class));
        }
        String netId = "student2";
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/modification/student/" + netId,
            List.class)).thenReturn(modificationsResponse);
        when(studentRepository.findStudentByNetId("student2")).thenReturn(studentModif);
        ModificationResponse result = studentServiceContract.getModificationsToRespond(netId);
        assertThat(result).isEqualTo(
            new ModificationResponse(studentModif, companyProposedModifications));
    }

    @Test
    public void testGetAllInvolvedInModifications() {
        List<LinkedHashMap> modificationsResponse = new ArrayList<>();
        for (ContractModification contractModification : studentInvolvedInProposals) {
            modificationsResponse.add(
                mapper.convertValue(contractModification, LinkedHashMap.class));
        }
        String netId = "student2";
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/modification/all/student/" + netId,
            List.class)).thenReturn(modificationsResponse);
        when(studentRepository.findStudentByNetId("student2")).thenReturn(studentModif);
        ModificationResponse result = studentServiceContract.getAllInvolvedInModifications(netId);
        assertThat(result).isEqualTo(
            new ModificationResponse(studentModif, studentInvolvedInProposals));
    }

    @Test
    public void testAcceptModification() {
        String studentId = "student2";
        Long modificationId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/student/" + studentId + "/accepts/"
                + modificationId, request, Contract.class)).thenReturn(resultedContract);
        Contract result = studentServiceContract.acceptModification(studentId, modificationId);
        assertThat(result).isEqualTo(resultedContract);

    }

    @Test
    public void testDeclineModification() {
        String netId = "student2";
        Long modificationId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/student/" + netId + "/declines/" + modificationId,
            request, ContractModification.class)).thenReturn(declinedCompanyModification);
        ContractModification result = studentServiceContract
                .declineModification(netId, modificationId);
        assertThat(result).isEqualTo(declinedCompanyModification);
    }

    @Test
    public void testGetAllContracts() {
        List<LinkedHashMap> list = new ArrayList<>();
        for (Contract c : contracts) {
            list.add(mapper.convertValue(c, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/contracts/student/" + studentId,
            List.class)).thenReturn(list);

        List<Contract> response = studentServiceContract.getAllContracts(studentId);
        assertEquals(contracts, response);
    }

    @Test
    public void testGetContract() {
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/contracts/" + c1.getContractId(),
            Contract.class)).thenReturn(c1);
        Contract response = studentServiceContract.getContract(c1.getContractId());
        assertEquals(c1, response);
    }

    @Test
    public void testGetTargetedRequests() {
        // Create new arraylist and add testing request
        List<LinkedHashMap> mapRequest = new ArrayList<>();
        mapRequest.add(mapper.convertValue(request, LinkedHashMap.class));

        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/student/targeted/targetId",
            List.class)).thenReturn(mapRequest);

        List<CompanyRequest> response = studentServiceRequest.getTargetedRequest("targetId");
        assertEquals(request.getTargetStudentId(), response.get(0).getTargetStudentId());
    }

    @Test
    public void testRequestChangesToCompanyRequest() {
        CompanyRequestModification modification = new
                CompanyRequestModification(studentId, 1L, 4, 16, 12.5, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(gson.toJson(modification), headers);
        when(restTemplate.postForObject("http://REQUEST-SERVICE/modification/" + 1L,
                request, CompanyRequestModification.class))
                .thenReturn(modification);
        CompanyRequestModification result = studentServiceRequest
                .requestChangesToCompanyRequest(studentId, 1L,
                        new CompanyRequestModification(4, 16, 12.5));
        assertEquals(result, modification);
        verify(restTemplate).postForObject("http://REQUEST-SERVICE/modification/" + 1L,
                request, CompanyRequestModification.class);
    }
}
