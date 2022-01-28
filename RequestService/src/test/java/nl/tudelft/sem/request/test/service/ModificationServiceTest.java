package nl.tudelft.sem.request.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.CompanyRequestModification;
import nl.tudelft.sem.request.repository.CompanyRequestModificationRepository;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import nl.tudelft.sem.request.service.ModificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ModificationServiceTest {

    private final Gson gson = new GsonBuilder().create();
    @Mock
    private CompanyRequestRepository companyRequestRepository;
    @Mock
    private CompanyRequestModificationRepository companyRequestModificationRepository;
    @InjectMocks
    private ModificationService modificationService;
    private final String payload = "{\n"
            + "        \"studentId\": \"revirator\",\n"
            + "        \"serviceId\": 1,\n"
            + "        \"hoursPerWeek\": 4,\n"
            + "        \"totalHours\": 16,\n"
            + "        \"pricePerHour\": 12.5,\n"
            + "        \"acceptedByCompany\": false\n"
            + "}";
    private final CompanyRequestModification modification = gson
            .fromJson(payload, CompanyRequestModification.class);
    private final String companyId = "amazon94";
    private final CompanyRequest companyRequest = new CompanyRequest();

    @Test
    public void testSaveModification() {
        companyRequest.setServiceId(1L);
        companyRequest.setCompanyId(companyId);
        given(companyRequestRepository.getOne(1L)).willReturn(companyRequest);
        given(companyRequestModificationRepository.save(modification)).willReturn(modification);
        CompanyRequestModification result = modificationService.saveModification(1L, modification);
        assertEquals(result, modification);
        assertEquals(result.getCompanyId(), companyId);
        verify(companyRequestRepository).getOne(1L);
        verify(companyRequestModificationRepository).save(modification);
    }

    @Test
    public void testAcceptChanges() {
        CompanyRequest updated = new CompanyRequest(4, 16, 12.5);
        given(companyRequestModificationRepository.getOne(2L)).willReturn(modification);
        given(companyRequestRepository.getOne(1L)).willReturn(companyRequest);
        given(companyRequestRepository.save(updated)).willReturn(updated);
        CompanyRequest result = modificationService.acceptChanges(companyId, 2L);
        assertEquals(result, updated);
        verify(companyRequestModificationRepository).getOne(2L);
        verify(companyRequestRepository).getOne(1L);
        verify(companyRequestRepository).delete(companyRequest);
        verify(companyRequestRepository).save(updated);
    }

    @Test
    public void testAcceptChangesThatAreInvalid() {
        CompanyRequestModification newModification = gson
                .fromJson(payload, CompanyRequestModification.class);
        newModification.setHoursPerWeek(0);
        newModification.setTotalHours(0);
        newModification.setPricePerHour(0);
        CompanyRequest updated = new CompanyRequest(5, 5, 5.5);
        given(companyRequestModificationRepository.getOne(2L)).willReturn(newModification);
        given(companyRequestRepository.getOne(1L)).willReturn(updated);
        given(companyRequestRepository.save(updated)).willReturn(updated);
        CompanyRequest result = modificationService.acceptChanges(companyId, 2L);
        assertEquals(result, updated);
        assertEquals(result.getHoursPerWeek(), 5);
        assertEquals(result.getTotalHours(), 5);
        assertEquals(result.getSalaryPerHour(), 5.5);
        verify(companyRequestModificationRepository).getOne(2L);
        verify(companyRequestRepository).getOne(1L);
        verify(companyRequestRepository).delete(updated);
        verify(companyRequestRepository).save(updated);
    }

    @Test
    public void testRejectChanges() {
        given(companyRequestModificationRepository.getOne(2L)).willReturn(modification);
        given(companyRequestRepository.getOne(1L)).willReturn(companyRequest);
        CompanyRequest result = modificationService.rejectChanges(companyId, 2L);
        assertEquals(result, companyRequest);
        verify(companyRequestModificationRepository).getOne(2L);
        verify(companyRequestModificationRepository).deleteById(2L);
    }

    @Test
    public void testGetAllModificationsForCompany() {
        given(companyRequestModificationRepository.findAllByCompanyId(companyId))
                .willReturn(List.of(modification));
        List<CompanyRequestModification> result = modificationService
                .getAllModificationsForCompany(companyId);
        assertThat(result).containsExactly(modification);
        verify(companyRequestModificationRepository).findAllByCompanyId(companyId);
    }
}
