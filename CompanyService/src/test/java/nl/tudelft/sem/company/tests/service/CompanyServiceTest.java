package nl.tudelft.sem.company.tests.service;

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.repository.CompanyRepository;
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
import nl.tudelft.sem.company.valueobjects.Student;
import nl.tudelft.sem.company.valueobjects.StudentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

    private final Gson gson = new GsonBuilder().create();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String studentUsername = "revirator";
    private final String username = "amazon94";
    private final Company c1 = new Company(username, "Amazon", 4.9);
    private final Company c2 = new Company("tudelft1842", "TU Delft", 4.2);
    private final Student student = new Student(studentUsername, "Denis Tsvetkov", 0, false, 3D);
    private final Feedback feedback = new Feedback(2L, 5, "Wow! Great job!", studentUsername);
    private final List<Feedback> feedbacks =
        List.of(feedback, new Feedback(1L, 1, "Would not recommend.", studentUsername));
    private final ContractModification postContractModification =
        new ContractModification("MODIFICATION", 1L, "student1", "company1", 15, 60, 5);
    private final ContractModification studentProposedModification =
        new ContractModification(2L, "EXTENSION", 2L, "student2", "company2", 15, 60, 5, false,
            true, false);
    private final ContractModification declinedStudentModification =
        new ContractModification(2L, "EXTENSION", 2L, "student2", "company2", 15, 60, 5, false,
            true, true);
    private final List<ContractModification> studentProposedModifications =
        List.of(studentProposedModification);
    private final List<ContractModification> companyInvolvedModifications =
        List.of(studentProposedModification, studentProposedModification);
    private final Company companyModif = new Company("company2", "Company Too", 4.5);
    private final Contract contract1 = new Contract(1L, 4, 16, 12.5, "student1", "Netflix", false);
    private final Contract resultedContract =
        new Contract(2L, 15, 60, 5, "student2", "company2", false);
    private final Contract contract4 = new Contract(3L, 6, 12, 13.5, "student3", "Netflix", false);
    List<Contract> contracts = List.of(contract1, contract4);
    private final CompanyRequest companyRequest = new CompanyRequest(1L, 10, 20,
            14, "Ismael", List.of("requirements"), new ArrayList<>(),   false, "studentId",
        "targetedId");
    private final CompanyRequest companyRequest2 = new CompanyRequest(2L, 15, 60,
            20, "Ismael", List.of("It", "B"), new ArrayList<>(), false, "", "");
    List<CompanyRequest> companyRequests = List.of(companyRequest, companyRequest2);
    private final String companyId = "Ismael";
    final StudentRequest studentRequest1 = new StudentRequest(2L, 14, 56, 12,
            "Ahmet", Arrays.asList("C++", "B"), false, "Apple");
    final StudentRequest studentRequest2 = new StudentRequest(2L, 10, 40, 12,
            "Ismael", Arrays.asList("C++", "B"), false, "Apple");
    List<StudentRequest> studentRequests = List.of(studentRequest1, studentRequest2);


    @Mock
    private RestTemplate restTemplate;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private CompanyService companyService;

    @Test
    public void testGetAll() {
        given(companyRepository.findAll()).willReturn(List.of(c1, c2));
        List<Company> companies = companyService.getAll();
        assertThat(companies).hasSameElementsAs(List.of(c1, c2));
        verify(companyRepository).findAll();
    }

    @Test
    public void testGetCompanyByUser() {
        given(companyRepository.findCompanyByUsername(username)).willReturn(c1);
        Company company = companyService.findCompanyByUsername(username);
        assertEquals(company, c1);
        verify(companyRepository).findCompanyByUsername(username);
    }

    @Test
    public void testPostCompanyByUser() {
        given(companyRepository.save(c1)).willReturn(c1);
        Company result = companyService.saveCompany(c1);
        assertThat(result.getUsername()).isNotNull();
        assertEquals(result.getUsername(), username);
        verify(companyRepository).save(c1);
    }

    @Test
    public void testGetFeedbackForStudent() {
        List<LinkedHashMap> feedbackResponse = new ArrayList<>();
        for (Feedback f : feedbacks) {
            feedbackResponse.add(mapper.convertValue(f, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + studentUsername,
            List.class)).thenReturn(feedbackResponse);
        when(restTemplate.getForObject("http://STUDENT-SERVICE/student/" + studentUsername + "/",
            Student.class)).thenReturn(student);
        FeedbackResponse response = companyService.getFeedbackForStudent(studentUsername);
        assertEquals(response, new FeedbackResponse(student, feedbacks));
    }

    @Test
    public void testPostFeedbackForStudent() {
        List<LinkedHashMap> feedbackResponse = new ArrayList<>();
        for (Feedback f : feedbacks) {
            feedbackResponse.add(mapper.convertValue(f, LinkedHashMap.class));
        }
        Feedback postFeedback = new Feedback(5, "Test feedback.");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String feedbackJsonObject = gson.toJson(postFeedback);
        HttpEntity<String> request = new HttpEntity<>(feedbackJsonObject, headers);
        when(restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + studentUsername,
            List.class)).thenReturn(feedbackResponse);
        when(restTemplate.postForObject("http://FEEDBACK-SERVICE/feedback/" + studentUsername,
            request, Feedback.class)).thenReturn(feedback);
        when(restTemplate.getForObject("http://STUDENT-SERVICE/student/" + studentUsername + "/",
            Student.class)).thenReturn(student);
        FeedbackResponse response =
            companyService.postFeedbackForStudent(studentUsername, postFeedback);
        assertThat(response.getFeedbacks()).contains(feedback);
        assertEquals(response, new FeedbackResponse(student, feedbacks));
    }

    @Test
    public void testDeleteAllCompanyRequests() {
        List<LinkedHashMap> requestResponse = new ArrayList<>();
        for (CompanyRequest r : companyRequests) {
            requestResponse.add(mapper.convertValue(r, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/company/" + companyId,
                List.class)).thenReturn(requestResponse);
        List<CompanyRequest> result = new ArrayList<>(companyService
                .deleteAllCompanyRequests(companyId));
        System.out.println(result);
        assertEquals(result, companyRequests);
    }

    @Test
    public void testDeleteCompanyRequest() {
        final CompanyRequest companyRequest =
                new CompanyRequest(1L, 20, 400, 4, "Google",
                        Arrays.asList("A", "B"), new ArrayList<>(), false, "", "");
        Long serviceId = 1L;
        String companyId = "Google";
        String companyId2 = "Issy";
        when(restTemplate.getForObject(
                "http://REQUEST-SERVICE/request/company/service/" + serviceId,
                CompanyRequest.class)).thenReturn(companyRequest);
        CompanyRequest result = companyService.deleteCompanyRequest(companyId, serviceId);
        CompanyRequest result2 = companyService.deleteCompanyRequest(companyId2, serviceId);
        assertThat(companyId).isEqualTo(result.getCompanyId());
        assertThat(result2).isNull();
    }

    @Test
    public void testSearchExpertiseStudents() {
        String expertise = "C++";
        StudentRequest request = new StudentRequest(1L, 12, 48, 10, "Ismael",
                List.of("A", "B"), false, "");
        List<StudentRequest> list = List.of(request);
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/company/search/" + expertise,
                List.class)).thenReturn(list);
        List<StudentRequest> result = companyService.searchExpertiseStudents(expertise);
        assertThat(list).isEqualTo(result);
    }

    @Test
    public void testGetContractsByCompanyUsername() {
        String companyId = "Netflix";
        List<LinkedHashMap> contractsResponse = new ArrayList<>();
        for (Contract c : contracts) {
            contractsResponse.add(mapper.convertValue(c, LinkedHashMap.class));
        }
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/contracts/company/" + companyId,
            List.class)).thenReturn(contractsResponse);
        ContractResponse result = companyService.getContractsByCompanyId(companyId);
        assertThat(contracts).hasSameElementsAs(result.getContracts());
    }

    @Test
    public void testGetCompanyRequest() {
        final CompanyRequest companyRequest =
            new CompanyRequest(1L, 20, 400, 4, "Google", Arrays.asList("A", "B"),
                    false, "");
        Long serviceId = 1L;
        when(restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/company/service/" + serviceId,
            CompanyRequest.class)).thenReturn(companyRequest);
        CompanyRequest result = companyService.getCompanyRequest(serviceId);
        assertThat(result).isEqualTo(companyRequest);
    }

    @Test
    public void testAcceptByCompany() {
        final CompanyRequest companyRequest2True =
            new CompanyRequest(1L, 20, 400, 4, "Google", Arrays.asList("A", "B"),
                    true, "revirator");
        Long serviceId = 1L;
        String companyId = "Google";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/company/acceptOwn/" + companyId + "/" + serviceId
                    + "/revirator", request, CompanyRequest.class))
                .thenReturn(companyRequest2True);
        when(restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/company/service/" + serviceId,
            CompanyRequest.class)).thenReturn(companyRequest2True);
        CompanyRequest result = companyService.acceptByCompany(companyId, serviceId, "revirator");
        assertThat(result).isEqualTo(companyRequest2True);
    }

    @Test
    public void testAcceptStudentRequest() {
        final StudentRequest studentRequest3 =
            new StudentRequest(2L, 14, 56, 12, "Ahmet", Arrays.asList("C++", "B"), false, "Apple");
        String companyId = "Apple";
        Long serviceId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://REQUEST-SERVICE/request/student/accept/" + companyId + "/" + serviceId, request,
            StudentRequest.class)).thenReturn(studentRequest3);
        when(restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/student/serviceId/" + serviceId,
            StudentRequest.class)).thenReturn(studentRequest3);
        StudentRequest result = companyService.acceptStudentRequest(companyId, serviceId);
        assertThat(result).isEqualTo(studentRequest3);

    }

    @Test
    public void testGetStudentRequest() {
        final StudentRequest studentRequest3 =
            new StudentRequest(2L, 14, 56, 12, "Ahmet", Arrays.asList("C++", "B"), false, "Apple");
        Long serviceId = 2L;
        when(restTemplate.getForObject(
            "http://REQUEST-SERVICE/request/student/serviceId/" + serviceId,
            StudentRequest.class)).thenReturn(studentRequest3);
        StudentRequest result = companyService.getStudentRequest(serviceId);
        assertThat(result).isEqualTo(studentRequest3);
    }

    @Test
    public void testProposeModification() {

        ContractModification proposedModification =
            new ContractModification("MODIFICATION", 1L, "student1", "company1", 15, 60, 5, true,
                false, false);
        ContractModification resultedModification =
            new ContractModification(1L, "MODIFICATION", 1L, "student1", "company1", 15, 60, 5,
                true, false, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String modificationJsonObject = gson.toJson(proposedModification);
        HttpEntity<String> request = new HttpEntity<>(modificationJsonObject, headers);

        when(restTemplate.postForObject("http://CONTRACT-SERVICE/modification/", request,
            ContractModification.class)).thenReturn(resultedModification);
        ContractModification response =
            companyService.proposeModification(postContractModification);
        assertEquals(response,
            new ContractModification(1L, "MODIFICATION", 1L, "student1", "company1", 15, 60, 5,
                true, false, false));
    }

    @Test
    public void testGetModificationsToRespond() {
        List<LinkedHashMap> modificationsResponse = new ArrayList<>();
        for (ContractModification contractModification : studentProposedModifications) {
            modificationsResponse.add(
                mapper.convertValue(contractModification, LinkedHashMap.class));
        }
        String companyId = "company2";
        when(restTemplate.getForObject("http://CONTRACT-SERVICE/modification/company/" + companyId,
            List.class)).thenReturn(modificationsResponse);
        when(companyRepository.findCompanyByUsername("company2")).thenReturn(companyModif);
        ModificationResponse result = companyService.getModificationsToRespond(companyId);
        assertThat(result).isEqualTo(
            new ModificationResponse(companyModif, studentProposedModifications));
    }

    @Test
    public void testGetAllInvolvedInModifications() {
        List<LinkedHashMap> modificationsResponse = new ArrayList<>();
        for (ContractModification contractModification : companyInvolvedModifications) {
            modificationsResponse.add(
                mapper.convertValue(contractModification, LinkedHashMap.class));
        }
        String companyId = "company2";
        when(restTemplate.getForObject(
            "http://CONTRACT-SERVICE/modification/all/company/" + companyId,
            List.class)).thenReturn(modificationsResponse);
        when(companyRepository.findCompanyByUsername("company2")).thenReturn(companyModif);
        ModificationResponse result = companyService.getAllInvolvedInModifications(companyId);
        assertThat(result).isEqualTo(
            new ModificationResponse(companyModif, companyInvolvedModifications));
    }

    @Test
    public void testAcceptModification() {
        String companyId = "company2";
        Long modificationId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/company/" + companyId + "/accepts/"
                + modificationId, request, Contract.class)).thenReturn(resultedContract);
        Contract result = companyService.acceptModification(companyId, modificationId);
        assertThat(result).isEqualTo(resultedContract);

    }

    @Test
    public void testDeclineModification() {
        String companyId = "company2";
        Long modificationId = 2L;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String json = "{}";
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        when(restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/company/" + companyId + "/declines/"
                + modificationId, request, ContractModification.class)).thenReturn(
            declinedStudentModification);
        ContractModification result = companyService.declineModification(companyId, modificationId);
        assertThat(result).isEqualTo(declinedStudentModification);

    }

    @Test
    public void testUpdateCompanyRequest() {
        Long serviceId = 1L;
        String companyId = "Facebook";
        CompanyRequest request =
                new CompanyRequest(1L, 10, 40, 10, "Facebook", List.of("IT"), new ArrayList<>(),
                    false,
                    "", "");
        CompanyRequestChange changes = new CompanyRequestChange();
        changes.setSalaryPerHour(35);
        when(restTemplate.getForObject("http://REQUEST-SERVICE/request/company/service/" + serviceId,
                CompanyRequest.class)).thenReturn(request);
        CompanyRequest result = companyService.updateCompanyRequest(companyId, serviceId, changes);
        assertEquals(result, request);
    }

    @Test
    public void testPostTargetedCompanyRequest() {
        String targetedId = "targetedId";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String companyRequestJsonObject = gson.toJson(companyRequest);
        HttpEntity<String> request = new HttpEntity<>(companyRequestJsonObject, headers);
        when(restTemplate.postForObject("http://REQUEST-SERVICE/request/company/targeted/"
                        + targetedId, request,
                CompanyRequest.class)).thenReturn(companyRequest);
        when(companyService.getCompanyRequest(companyRequest.getServiceId()))
                .thenReturn(companyRequest);

        CompanyRequest result = companyService
                .postTargetedCompanyRequest(companyRequest, targetedId);
        assertThat(result).isEqualTo(companyRequest);
    }

    @Test
    public void testGetRequestsByTotalHours() {
        List<LinkedHashMap> requests = new ArrayList<>();
        for (StudentRequest c : studentRequests) {
            requests.add(mapper.convertValue(c, LinkedHashMap.class));
        }

        FilterTag filterTag = new FilterTag("hoursPerWeek", 10, 15);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String requestJsonObject = gson.toJson(filterTag);
        HttpEntity<String> request = new HttpEntity<>(requestJsonObject, headers);

        when(restTemplate.postForObject("http://REQUEST-SERVICE/request/student/filter/", request,
                List.class)).thenReturn(requests);

        List<StudentRequest> response = companyService.searchAvailableStudents(filterTag);
        assertTrue(response.get(0).getHoursPerWeek() >= 10
                && response.get(0).getHoursPerWeek() <= 15);
    }



    @Test
    public void testAcceptSuggestedChanges() {
        final CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyId(username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        when(restTemplate.postForObject(
                "http://REQUEST-SERVICE/modification/company/" + username + "/accepts/" + 1L,
                request, CompanyRequest.class))
                .thenReturn(companyRequest);
        CompanyRequest result = companyService.acceptSuggestedChanges(username, 1L);
        assertEquals(result, companyRequest);
        verify(restTemplate).postForObject(
                "http://REQUEST-SERVICE/modification/company/" + username + "/accepts/" + 1L,
                request, CompanyRequest.class);
    }

    @Test
    public void testRejectSuggestedChanges() {
        final CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setCompanyId(username);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>("{}", headers);
        when(restTemplate.postForObject(
                "http://REQUEST-SERVICE/modification/company/" + username + "/rejects/" + 2L,
                request, CompanyRequest.class))
                .thenReturn(companyRequest);
        CompanyRequest result = companyService.rejectSuggestedChanges(username, 2L);
        assertEquals(result, companyRequest);
        verify(restTemplate).postForObject(
                "http://REQUEST-SERVICE/modification/company/" + username + "/rejects/" + 2L,
                request, CompanyRequest.class);
    }

    @Test
    public void testGetAllModificationsForCompany() {
        CompanyRequestModification modification = new CompanyRequestModification(15, 16, 20.5);
        LinkedHashMap linkedHashMap = mapper.convertValue(modification, LinkedHashMap.class);
        when(restTemplate.getForObject("http://REQUEST-MICROSERVICE/"
                + "modification/company/" + username + "/all", List.class))
                .thenReturn(List.of(linkedHashMap));
        List<CompanyRequestModification> result = companyService.getAllJobModifications(username);
        assertThat(result).containsExactly(modification);
        verify(restTemplate).getForObject("http://REQUEST-MICROSERVICE/"
                + "modification/company/" + username + "/all", List.class);
    }
}

