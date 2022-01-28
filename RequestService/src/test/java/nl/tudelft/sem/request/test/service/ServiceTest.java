package nl.tudelft.sem.request.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import nl.tudelft.sem.request.repository.StudentRequestRepository;
import nl.tudelft.sem.request.service.CompanyRequestService;
import nl.tudelft.sem.request.service.StudentRequestService;
import nl.tudelft.sem.request.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.request.valueobjects.Contract;
import nl.tudelft.sem.request.valueobjects.StudentRequestChange;
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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    private final Gson gson = new GsonBuilder().create();
    private final String companyId = "Google";
    //LIST OF STUDENT REQUESTS
    private final StudentRequest studentRequest =
        new StudentRequest(1L, 14, 56, 10, "Bogdan", Arrays.asList("A", "B"));
    private final CompanyRequest companyRequest2 =
        new CompanyRequest(3L, 15, 450, 9, "Facebook", Arrays.asList("A", "B"));
    private final String studentId = "Bogdan";
    //SOME COMMON VARIABLES USED
    private final String facebook = "Facebook";
    //LIST OF COMPANY REQUESTS
    private final Long serviceId = 1L;
    private final CompanyRequest companyRequest1 =
        new CompanyRequest(1L, 20, 400, 4, facebook, Arrays.asList("A", "B"));
    private final String microsoft = "Microsoft";
    private final String ahmet = "Ahmet";
    private final StudentRequest studentRequest2 =
        new StudentRequest(2L, 14, 56, 10, ahmet, Arrays.asList("A", "B"));
    private final StudentRequest studentRequest3 =
        new StudentRequest(3L, 14, 56, 12, ahmet, Arrays.asList("B"));
    private final List<StudentRequest> emptyList = List.of();
    private String revirator = "revirator";
    @Mock
    private StudentRequestRepository studentRequestRepository;
    @Mock
    private CompanyRequestRepository companyRequestRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CompanyRequestService companyRequestService;
    @InjectMocks
    private StudentRequestService studentRequestService;

    @Test
    public void testGetAllCompanyRequests() {
        given(companyRequestRepository.findAll()).willReturn(
            List.of(companyRequest1, companyRequest2));
        List<CompanyRequest> result = companyRequestService.getAllCompanyRequests();
        assertThat(result).hasSameElementsAs(List.of(companyRequest1, companyRequest2));
        verify(companyRequestRepository).findAll();
    }

    @Test
    public void testGetAllStudentRequests() {
        given(studentRequestRepository.findAll()).willReturn(
            List.of(studentRequest, studentRequest2));
        List<StudentRequest> result = studentRequestService.getAllStudentRequests();
        assertThat(result).hasSameElementsAs(List.of(studentRequest, studentRequest2));
        verify(studentRequestRepository).findAll();
    }

    @Test
    public void testFindRequestForCompany() {
        given(companyRequestRepository.findAllByCompanyId(companyId)).willReturn(
            List.of(companyRequest2));
        List<CompanyRequest> result = companyRequestService.findAllRequestByCompanyId(companyId);
        assertThat(result).hasSameElementsAs(List.of(companyRequest2));
        verify(companyRequestRepository).findAllByCompanyId(companyId);
    }

    @Test
    public void testFindCompanyRequestFilteredByTotalHours() {
        given(companyRequestRepository.findAllByTotalHoursBetween(390, 410)).willReturn(
            List.of(companyRequest1));
        List<CompanyRequest> result =
            companyRequestService.findAllCompanyRequestsByTotalHours(390, 410);
        assertThat(result).hasSameElementsAs(List.of(companyRequest1));
        verify(companyRequestRepository).findAllByTotalHoursBetween(390, 410);
    }

    @Test
    public void testFindCompanyRequestFilteredByHoursPerWeek() {
        given(companyRequestRepository.findAllByHoursPerWeekBetween(14, 16)).willReturn(
            List.of(companyRequest2));
        List<CompanyRequest> result =
            companyRequestService.findAllCompanyRequestsByHoursPerWeek(14, 16);
        assertThat(result).hasSameElementsAs(List.of(companyRequest2));
        verify(companyRequestRepository).findAllByHoursPerWeekBetween(14, 16);

    }

    @Test
    public void testFindCompanyRequestFilteredBySalaryPerHour() {
        given(companyRequestRepository.findAllBySalaryPerHourBetween(8, 10)).willReturn(
            List.of(companyRequest2));
        List<CompanyRequest> result =
            companyRequestService.findAllCompanyRequestsBySalaryPerHour(8, 10);
        assertThat(result).hasSameElementsAs(List.of(companyRequest2));
        verify(companyRequestRepository).findAllBySalaryPerHourBetween(8, 10);

    }

    @Test

    public void testSaveCompanyRequest() {
        given(companyRequestRepository.save(companyRequest1)).willReturn(companyRequest1);
        CompanyRequest result = companyRequestService.saveCompanyRequest(companyRequest1);
        assertThat(result.getCompanyId()).isNotNull();
        assertEquals(1, result.getServiceId());
        verify(companyRequestRepository).save(companyRequest1);
    }

    @Test
    public void testSalaryUpdateStudentRequest() {
        given(studentRequestRepository.findByStudentId(studentId)).willReturn(studentRequest);
        given(studentRequestRepository.save(studentRequest)).willReturn(studentRequest);
        StudentRequestChange changes = new StudentRequestChange();
        changes.setSalaryPerHour(20);
        StudentRequest updatedRequest =
            studentRequestService.updateStudentRequest(studentId, changes);
        assertEquals(20, updatedRequest.getSalaryPerHour());
        assertEquals(studentRequest.getExpertise(), updatedRequest.getExpertise());
        verify(studentRequestRepository, times(2)).findByStudentId(studentId);
        verify(studentRequestRepository).save(studentRequest);
    }

    @Test
    public void testUpdateCompanyRequest() {
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(companyRequest1);
        given(companyRequestRepository.save(companyRequest1)).willReturn(companyRequest1);
        CompanyRequestChange changes = new CompanyRequestChange(11, 300, 6, List.of("Java"));
        CompanyRequest updatedRequest =
            companyRequestService.updateCompanyRequest(serviceId, changes);
        assertEquals(11, updatedRequest.getHoursPerWeek());
        assertEquals(300, updatedRequest.getTotalHours());
        assertEquals(6, updatedRequest.getSalaryPerHour());
        assertThat(updatedRequest.getRequirements()).containsExactly("Java");
        verify(companyRequestRepository, times(2)).findByServiceId(serviceId);
        verify(companyRequestRepository).save(companyRequest1);
    }

    @Test
    public void testNullUpdateStudentRequest() {
        given(studentRequestRepository.findByStudentId(studentId)).willReturn(null);
        StudentRequestChange changes = new StudentRequestChange();
        changes.setSalaryPerHour(20);
        StudentRequest updatedRequest =
            studentRequestService.updateStudentRequest(studentId, changes);
        assertNull(updatedRequest);
        verify(studentRequestRepository, times(1)).findByStudentId(studentId);
        verify(studentRequestRepository, times(0)).save(null);
    }

    @Test
    public void testWrongUpdateStudentRequest() {
        given(studentRequestRepository.findByStudentId(studentId)).willReturn(studentRequest);
        StudentRequestChange changes = new StudentRequestChange();
        changes.setSalaryPerHour(-1);
        changes.setTotalHours(-1);
        changes.setExpertise(null);
        changes.setHoursPerWeek(-1);
        StudentRequest updatedRequest =
            studentRequestService.updateStudentRequest(studentId, changes);
        assertEquals(studentRequest, updatedRequest);
        verify(studentRequestRepository, times(2)).findByStudentId(studentId);
        verify(studentRequestRepository, times(1)).save(studentRequest);
    }

    @Test
    public void testGoodTermsUpdateStudentRequest() {
        given(studentRequestRepository.findByStudentId(studentId)).willReturn(studentRequest);
        StudentRequestChange changes = new StudentRequestChange();
        changes.setTotalHours(20);
        changes.setExpertise(List.of("C++"));
        changes.setHoursPerWeek(4);
        StudentRequest updatedRequest =
            studentRequestService.updateStudentRequest(studentId, changes);
        assertEquals(changes.getHoursPerWeek(), updatedRequest.getHoursPerWeek());
        assertEquals(changes.getExpertise(), updatedRequest.getExpertise());
        assertEquals(changes.getTotalHours(), updatedRequest.getTotalHours());
        assertEquals(studentRequest.getSalaryPerHour(), updatedRequest.getSalaryPerHour());
        verify(studentRequestRepository, times(2)).findByStudentId(studentId);
        verify(studentRequestRepository, times(1)).save(updatedRequest);
    }

    @Test
    public void testSaveStudentRequest() {
        given(studentRequestRepository.save(studentRequest)).willReturn(studentRequest);
        StudentRequest result = studentRequestService.saveStudentRequest(studentRequest);
        assertThat(result.getCompanyId()).isNotNull();
        assertEquals(1, result.getServiceId());
        verify(studentRequestRepository).save(studentRequest);
    }

    @Test
    public void testFindRequestByStudent() {
        given(studentRequestRepository.findByStudentId(ahmet)).willReturn(studentRequest2);
        StudentRequest result = studentRequestService.findRequestByStudent(ahmet);
        assertEquals(result, studentRequest2);
        verify(studentRequestRepository).findByStudentId(ahmet);
    }

    @Test
    public void testAcceptCompanyRequest() {
        //First, the company request will not have a studentId associated with it.
        CompanyRequest c1 = new CompanyRequest(1L, 20, 400, 4, facebook, Arrays.asList("A", "B"));
        Long serviceId = 1L;
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(c1);
        CompanyRequest c2 = new CompanyRequest(1L, 20, 400, 4, facebook, Arrays.asList("A", "B"));
        //Company request will have an associated studentId with it once the student accepts.
        c2.addStudentId(revirator);
        given(companyRequestRepository.saveAndFlush(c2)).willReturn(c2);
        String studentId = revirator;
        CompanyRequest result = companyRequestService.acceptCompanyRequest(studentId, serviceId);
        assertThat(result).isEqualTo(c2);
    }

    @Test
    public void testSendGeneratedContract() {
        Contract contract = new Contract(1L, 16, 48, 13, revirator, microsoft, false);
        String contractJsonObject = gson.toJson(contract);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(contractJsonObject, headers);
        given(restTemplate.postForObject("http://CONTRACT-SERVICE/contracts/", request,
            Contract.class)).willReturn(contract);
        Contract result = companyRequestService.sendGeneratedContract(contract);
        assertThat(result).isEqualTo(contract);
    }

    @Test
    public void testAcceptOwnRequestCompany() {
        final CompanyRequest companyRequest1 =
            new CompanyRequest(1L, 20, 400, 4, facebook, Arrays.asList("A", "B"));
        companyRequest1.setStudentId(revirator);
        Long serviceId = 1L;
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(companyRequest1);
        companyRequest1.setAcceptedByCompany(true);
        String companyId = facebook;
        CompanyRequest result =
            companyRequestService.acceptOwnRequestCompany(companyId, studentId, serviceId);
        assertThat(result).isEqualTo(companyRequest1);
    }

    @Test
    public void testRejectStudentAsCompany() {
        CompanyRequest newCompanyRequest =
            new CompanyRequest(1L, 20, 400, 4, facebook, Arrays.asList("A", "B"));
        newCompanyRequest.setStudentIdCandidates(List.of(ahmet));
        companyRequest1.setStudentIdCandidates(List.of(revirator, ahmet));
        companyRequest1.setAcceptedByCompany(true);
        given(companyRequestRepository.findByServiceId(1L)).willReturn(companyRequest1);
        given(companyRequestRepository.saveAndFlush(newCompanyRequest))
                .willReturn(newCompanyRequest);
        CompanyRequest result = companyRequestService.rejectStudentAsCompany(1L, revirator);
        assertThat(result).isEqualTo(newCompanyRequest);
        assertThat(result.getStudentIdCandidates()).containsExactly(ahmet);
        assertFalse(result.isAcceptedByCompany());
    }

    @Test
    public void testAcceptStudentRequest() {
        Long serviceId = 2L;
        final StudentRequest studentRequest3 =
            new StudentRequest(2L, 14, 56, 12, ahmet, Arrays.asList("B"));
        StudentRequest studentRequest4 =
            new StudentRequest(2L, 14, 56, 12, ahmet, Arrays.asList("B"));
        studentRequest4.setCompanyId(microsoft);
        given(studentRequestRepository.findByServiceId(serviceId)).willReturn(studentRequest3);
        given(studentRequestRepository.saveAndFlush(studentRequest4)).willReturn(studentRequest4);
        StudentRequest result = studentRequestService.acceptStudentRequest(microsoft, serviceId);
        assertThat(result).isEqualTo(studentRequest4);
    }

    @Test
    public void testAcceptOwnRequestStudent() {
        StudentRequest studentRequest3 =
            new StudentRequest(2L, 14, 56, 12, ahmet, Arrays.asList("Python", "B"));
        studentRequest3.setCompanyId(facebook);
        Long serviceId = 2L;
        given(studentRequestRepository.findByServiceId(serviceId)).willReturn(studentRequest3);
        studentRequest3.setAcceptedByStudent(true);
        StudentRequest result = studentRequestService.acceptOwnRequestStudent(ahmet, serviceId);
        assertThat(result).isEqualTo(studentRequest3);
    }

    @Test
    public void testRejectCompanyAsStudent() {
        StudentRequest newStudentRequest =
            new StudentRequest(3L, 14, 56, 12, ahmet, Arrays.asList("B"));
        newStudentRequest.setCompanyId("");
        newStudentRequest.setAcceptedByStudent(false);
        studentRequest3.setCompanyId(facebook);
        studentRequest3.setAcceptedByStudent(true);
        given(studentRequestRepository.findByServiceId(2L)).willReturn(studentRequest3);
        given(studentRequestRepository.saveAndFlush(newStudentRequest))
                .willReturn(newStudentRequest);
        StudentRequest result = studentRequestService.rejectCompanyAsStudent(2L);
        assertThat(result).isEqualTo(newStudentRequest);
        assertEquals(result.getCompanyId(), "");
        assertFalse(result.isAcceptedByStudent());
    }

    @Test
    public void testFindStudentRequestByServiceId() {
        StudentRequest studentRequest3 =
            new StudentRequest(2L, 14, 56, 12, ahmet, Arrays.asList("PHP", "B"));
        given(studentRequestRepository.findByServiceId(2L)).willReturn(studentRequest3);
        StudentRequest result = studentRequestService.findStudentRequestByServiceId(2L);
        assertThat(result).isEqualTo(studentRequest3);
    }

    @Test
    public void testFindCompanyRequestByServiceId() {
        when(companyRequestRepository.findByServiceId(1L)).thenReturn(companyRequest1);
        CompanyRequest result = companyRequestService.findCompanyRequestByServiceId(1L);
        assertThat(result).isEqualTo(companyRequest1);
        verify(companyRequestRepository).findByServiceId(1L);
    }

    @Test
    public void testFindTargetedRequest() {
        when(companyRequestRepository.findCompanyRequestsByTargetStudentId("studentId")).thenReturn(
            List.of(companyRequest1));

        List<CompanyRequest> result =
            companyRequestService.findTargetedRequestsForStudent("studentId");
        assertThat(result.get(0)).isEqualTo(companyRequest1);
        verify(companyRequestRepository).findCompanyRequestsByTargetStudentId("studentId");
    }

    @Test
    public void testNullDeleteStudentRequest() {
        when(studentRequestRepository.findByStudentId(studentId)).thenReturn(null);
        assertEquals(null, studentRequestService.deleteStudentRequest(studentId));
        verify(studentRequestRepository).findByStudentId(studentId);
        verify(studentRequestRepository, times(0)).delete(studentRequest);
    }

    @Test
    public void testDeleteStudentRequest() {
        when(studentRequestRepository.findByStudentId(studentId)).thenReturn(studentRequest);
        assertEquals(studentRequest, studentRequestService.deleteStudentRequest(studentId));
        verify(studentRequestRepository).findByStudentId(studentId);
        verify(studentRequestRepository).delete(studentRequest);
    }

    @Test
    public void testEmptySearchExpertiseStudents() {
        when(studentRequestRepository.findAll()).thenReturn(List.of());
        assertEquals(emptyList, studentRequestService.searchExpertiseStudents("C++"));
        verify(studentRequestRepository).findAll();
    }

    @Test
    public void testSearchForExpertiseStudents() {
        when(studentRequestRepository.findAll()).thenReturn(
            List.of(studentRequest, studentRequest3));
        assertEquals(List.of(studentRequest), studentRequestService.searchExpertiseStudents("A"));
        verify(studentRequestRepository).findAll();
    }

    @Test
    public void testPostTargetedCompanyRequest() {
        String targetedId = "targetedId";
        when(companyRequestRepository.save(companyRequest1)).thenReturn(companyRequest1);

        CompanyRequest result =
            companyRequestService.saveTargetedCompanyRequest(companyRequest1, targetedId);
        assertThat(result.getTargetStudentId()).isEqualTo(targetedId);
    }

    @Test
    public void testFindAllRequestByHours() {
        given(studentRequestRepository.findAllByHoursPerWeekBetween(15, 21)).willReturn(
            List.of(studentRequest3));
        List<StudentRequest> result = studentRequestService.findAllStudentRequestsByHours(15, 21);
        assertThat(result).hasSameElementsAs(List.of(studentRequest3));
        verify(studentRequestRepository).findAllByHoursPerWeekBetween(15, 21);
    }

    @Test
    public void testDeleteAllCompanyRequests() {
        given(companyRequestRepository.findAllByCompanyId(facebook)).willReturn(
                List.of(companyRequest1, companyRequest2));
        List<CompanyRequest> result = companyRequestService.deleteAllCompanyRequests(facebook);
        assertThat(result).containsExactly(companyRequest1, companyRequest2);
        verify(companyRequestRepository).findAllByCompanyId(facebook);
        verify(companyRequestRepository).delete(companyRequest1);
        verify(companyRequestRepository).delete(companyRequest2);
    }

    @Test
    public void testDeleteCompanyRequestThatExists() {
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(companyRequest1);
        CompanyRequest result = companyRequestService.deleteCompanyRequest(serviceId);
        assertThat(result).isEqualTo(companyRequest1);
        verify(companyRequestRepository).findByServiceId(serviceId);
        verify(companyRequestRepository).delete(companyRequest1);
    }

    @Test
    public void testDeleteCompanyRequestThatDoesNotExist() {
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(null);
        CompanyRequest result = companyRequestService.deleteCompanyRequest(serviceId);
        assertNull(result);
        verify(companyRequestRepository).findByServiceId(serviceId);
        verify(companyRequestRepository, never()).delete(companyRequest1);
    }

    @Test
    public void testGetStudentIdCandidates() {
        companyRequest1.setStudentIdCandidates(List.of(ahmet, revirator));
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(companyRequest1);
        List<String> result = companyRequestService.getStudentIdCandidates(serviceId);
        assertThat(result).containsExactlyInAnyOrder(revirator, ahmet);
        verify(companyRequestRepository).findByServiceId(serviceId);
    }

    @Test
    public void testUpdateCompanyRequestThatDoesNotExist() {
        CompanyRequestChange changes = new CompanyRequestChange();
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(null);
        CompanyRequest result = companyRequestService.updateCompanyRequest(serviceId, changes);
        assertNull(result);
        verify(companyRequestRepository, atMostOnce()).findByServiceId(serviceId);
        verify(companyRequestRepository, never()).save(null);
    }

    @Test
    public void testUpdateCompanyRequestWithInvalidChanges() {
        CompanyRequestChange changes = new CompanyRequestChange(-1, -1, -1, null);
        given(companyRequestRepository.findByServiceId(serviceId)).willReturn(companyRequest1);
        given(companyRequestRepository.save(companyRequest1)).willReturn(companyRequest1);
        CompanyRequest result = companyRequestService.updateCompanyRequest(serviceId, changes);
        assertEquals(result, companyRequest1);
        assertEquals(result.getHoursPerWeek(), 20);
        assertEquals(result.getTotalHours(), 400);
        assertEquals(result.getSalaryPerHour(), 4);
        assertThat(result.getRequirements()).containsExactlyInAnyOrder("A", "B");
        verify(companyRequestRepository, times(2)).findByServiceId(serviceId);
        verify(companyRequestRepository).save(companyRequest1);
    }
}
