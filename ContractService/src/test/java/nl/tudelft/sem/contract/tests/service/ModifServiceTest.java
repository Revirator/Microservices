package nl.tudelft.sem.contract.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.entity.ContractModification;
import nl.tudelft.sem.contract.repository.ContractRepository;
import nl.tudelft.sem.contract.repository.ModificationRepository;
import nl.tudelft.sem.contract.service.ModificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class ModifServiceTest {

    String typeOfModification = "MODIFICATION";
    String companyId4 = "company4";
    String studentId4 = "student4";
    ContractModification contractModif1 =
        new ContractModification(1L, typeOfModification, 1L, "student1", "company1", 15, 60, 5,
            true, false, false);
    ContractModification contractModif2 =
        new ContractModification(2L, typeOfModification, 2L, "student2", "company2", 15, 60, 5,
            false, true, false);
    Contract contract = new Contract(4L, 15, 60, 15, "student4", "company4", false);
    ContractModification contractModif3 =
        new ContractModification(3L, "TERMINATION", 4L, studentId4, companyId4, 15, 60, 5, true,
            false, false);

    ContractModification contractModif4 =
        new ContractModification(4L, "EXTENSION", 4L, studentId4, companyId4, 15, 90, 5, false,
            true, false);
    ContractModification contractModif5 =
        new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
            true, false, false);
    ContractModification contractModif6 =
        new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
            true, false, true);
    ContractModification contractModif7 =
        new ContractModification(5L, "MODIFICATIONnnnn", 4L, studentId4, companyId4, 10, 40, 3,
            true, false, false);
    ContractModification contractModif8 =
        new ContractModification(5L, "MODIFICATION", 4L, studentId4, companyId4, 10, 250, 3, true,
            false, false);
    ContractModification contractModif9 =
        new ContractModification(5L, "MODIFICATION", 4L, studentId4, companyId4, 30, 250, 3, true,
            false, false);
    ContractModification notAcceptedByStudentModif =
        new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
            false, false, false);
    ContractModification notAcceptedByCompanyModif =
        new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
            false, false, false);
    String companyId = "company2";
    String studentId = "student1";

    @Mock
    private ModificationRepository modificationRepository;
    @Mock
    private ContractRepository contractRepository;
    private ModificationService modificationService;

    @BeforeEach
    public void setup() {
        modificationService = new ModificationService(modificationRepository, contractRepository);
    }

    @Test
    public void testSaveModification() {
        given(modificationRepository.save(contractModif2)).willReturn(contractModif2);
        ContractModification contractModification =
            modificationService.saveModification(contractModif2);
        assertThat(contractModification).isEqualTo(contractModif2);
        verify(modificationRepository).save(contractModif2);

    }

    @Test
    public void testSaveTermination() {
        given(modificationRepository.save(contractModif3)).willReturn(contractModif3);
        ContractModification contractModification =
            modificationService.saveModification(contractModif3);
        assertThat(contractModification).isEqualTo(contractModif3);
        verify(modificationRepository).save(contractModif3);

    }

    @Test
    public void testSaveExtension() {
        given(modificationRepository.save(contractModif4)).willReturn(contractModif4);
        ContractModification contractModification =
            modificationService.saveModification(contractModif4);
        assertThat(contractModification).isEqualTo(contractModif4);
        verify(modificationRepository).save(contractModif4);

    }

    @Test
    public void testSaveWrongDuration() {
        ContractModification contractModification =
            modificationService.saveModification(contractModif8);
        assertThat(contractModification).isEqualTo(null);
        verify(modificationRepository, times(0)).save(contractModif8);
    }

    @Test
    public void testSaveWrongModification() {
        ContractModification contractModification =
            modificationService.saveModification(contractModif7);
        assertThat(contractModification).isEqualTo(null);
        verify(modificationRepository, times(0)).save(contractModif7);

    }

    @Test
    public void testSaveWrongHoursPerWeek() {
        ContractModification contractModification =
            modificationService.saveModification(contractModif9);
        assertThat(contractModification).isEqualTo(null);
        verify(modificationRepository, times(0)).save(contractModif9);

    }

    @Test
    public void testUpdateModification() {
        given(modificationRepository.saveAndFlush(contractModif2)).willReturn(contractModif2);
        ContractModification contractModification =
            modificationService.updateModification(contractModif2);
        assertThat(contractModification).isEqualTo(contractModif2);
        verify(modificationRepository).saveAndFlush(contractModif2);

    }

    @Test
    public void testDeleteContract() {

        doNothing().when(modificationRepository).delete(isA(ContractModification.class));
        modificationService.deleteModification(contractModif2);
        verify(modificationRepository).delete(contractModif2);

    }

    @Test
    public void testGetContractModification() {
        given(modificationRepository.getOne(1L)).willReturn(contractModif1);
        ContractModification contractModification = modificationService.getContractModification(1L);
        assertThat(contractModification).isEqualTo(contractModif1);
        verify(modificationRepository).getOne(1L);
    }

    @Test
    public void testGetProposalsForStudent() {
        given(modificationRepository.findAllByStudentIdAndAcceptedByCompanyAndFinished(studentId,
            true, false)).willReturn(List.of(contractModif1));
        List<ContractModification> contractModifications =
            modificationService.getProposalsForStudent(studentId);
        assertThat(contractModifications).isEqualTo(List.of(contractModif1));
        verify(modificationRepository).findAllByStudentIdAndAcceptedByCompanyAndFinished(studentId,
            true, false);

    }

    @Test
    public void testGetProposalsForCompany() {
        given(modificationRepository.findAllByCompanyIdAndAcceptedByStudentAndFinished(companyId,
            true, false)).willReturn(List.of(contractModif2));
        List<ContractModification> contractModifications =
            modificationService.getProposalsForCompany(companyId);
        assertThat(contractModifications).isEqualTo(List.of(contractModif2));
        verify(modificationRepository).findAllByCompanyIdAndAcceptedByStudentAndFinished(companyId,
            true, false);
    }

    @Test
    public void acceptTermination() {
        given(contractRepository.existsById(4L)).willReturn(true);
        given(contractRepository.getOne(4L)).willReturn(contract);
        Contract modifiedContract = new Contract(4L, 15, 60, 15, studentId4, companyId4, true);
        ContractModification acceptedModification =
            new ContractModification(3L, "TERMINATION", 4L, studentId4, companyId4, 15, 60, 5, true,
                true, true);
        given(contractRepository.saveAndFlush(modifiedContract)).willReturn(modifiedContract);
        given(modificationRepository.saveAndFlush(acceptedModification)).willReturn(
            acceptedModification);
        given(modificationRepository.getOne(3L)).willReturn(contractModif3);
        Contract terminated = modificationService.acceptModification(3L, false);
        assertThat(terminated).isEqualTo(modifiedContract);
        verify(contractRepository).existsById(4L);
        verify(contractRepository).getOne(4L);
        verify(contractRepository).saveAndFlush(modifiedContract);
        verify(modificationRepository).saveAndFlush(acceptedModification);
        verify(modificationRepository).getOne(3L);
    }

    @Test
    public void acceptExtension() {
        given(contractRepository.existsById(4L)).willReturn(true);
        given(contractRepository.getOne(4L)).willReturn(contract);
        given(modificationRepository.getOne(4L)).willReturn(contractModif4);
        Contract modifiedContract = new Contract(4L, 15, 90, 15, studentId4, companyId4, false);
        ContractModification acceptedModification =
            new ContractModification(4L, "EXTENSION", 4L, studentId4, companyId4, 15, 90, 5, true,
                true, true);

        given(contractRepository.saveAndFlush(modifiedContract)).willReturn(modifiedContract);
        given(modificationRepository.saveAndFlush(acceptedModification)).willReturn(
            acceptedModification);
        Contract extended = modificationService.acceptModification(4L, true);
        assertThat(extended).isEqualTo(modifiedContract);
        verify(contractRepository).existsById(4L);
        verify(contractRepository).getOne(4L);
        verify(contractRepository).saveAndFlush(modifiedContract);
        verify(modificationRepository).saveAndFlush(acceptedModification);
        verify(modificationRepository).getOne(4L);
    }

    @Test
    public void acceptModification() {
        given(contractRepository.existsById(4L)).willReturn(true);
        given(contractRepository.getOne(4L)).willReturn(contract);
        ContractModification acceptedModification =
            new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
                true, true, true);
        Contract modifiedContract = new Contract(4L, 10, 40, 3, studentId4, companyId4, false);
        given(contractRepository.saveAndFlush(modifiedContract)).willReturn(modifiedContract);
        given(modificationRepository.saveAndFlush(acceptedModification)).willReturn(
            acceptedModification);
        given(modificationRepository.getOne(5L)).willReturn(contractModif5);
        Contract modified = modificationService.acceptModification(5L, false);
        assertThat(modified).isEqualTo(modifiedContract);
        verify(contractRepository).existsById(4L);
        verify(contractRepository).getOne(4L);
        verify(contractRepository).saveAndFlush(modifiedContract);
        verify(modificationRepository).saveAndFlush(acceptedModification);
        verify(modificationRepository).getOne(5L);
    }

    @Test
    public void noExistContractTest() {
        ContractModification modified5 =
            new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
                true, true, true);
        given(contractRepository.existsById(4L)).willReturn(false);
        given(modificationRepository.getOne(5L)).willReturn(contractModif5);
        given(modificationRepository.saveAndFlush(modified5)).willReturn(modified5);
        Contract modified = modificationService.acceptModification(5L, false);
        assertThat(modified).isEqualTo(null);
        verify(contractRepository).existsById(4L);
        verify(modificationRepository).saveAndFlush(modified5);
        verify(modificationRepository).getOne(5L);
    }

    @Test
    public void rejectedModificationTest() {
        ContractModification modified6 =
            new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
                true, true, true);
        given(modificationRepository.saveAndFlush(modified6)).willReturn(modified6);
        given(modificationRepository.getOne(5L)).willReturn(contractModif6);
        Contract modified = modificationService.acceptModification(5L, false);
        assertThat(modified).isEqualTo(null);
        verify(modificationRepository).saveAndFlush(modified6);
        verify(modificationRepository).getOne(5L);
    }

    @Test
    public void notAcceptedByStudentTest() {
        ContractModification modified =
            new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
                true, false, true);
        given(modificationRepository.saveAndFlush(modified)).willReturn(modified);
        given(modificationRepository.getOne(5L)).willReturn(notAcceptedByStudentModif);
        Contract result = modificationService.acceptModification(5L, true);
        assertThat(result).isEqualTo(null);
        verify(modificationRepository).saveAndFlush(modified);
        verify(modificationRepository).getOne(5L);
    }

    @Test
    public void notAcceptedByCompanyTest() {
        ContractModification modified =
            new ContractModification(5L, typeOfModification, 4L, studentId4, companyId4, 10, 40, 3,
                false, true, true);
        given(modificationRepository.saveAndFlush(modified)).willReturn(modified);
        given(modificationRepository.getOne(5L)).willReturn(notAcceptedByCompanyModif);
        Contract result = modificationService.acceptModification(5L, false);
        assertThat(result).isEqualTo(null);
        verify(modificationRepository).saveAndFlush(modified);
        verify(modificationRepository).getOne(5L);
    }

    @Test
    public void wrongTypeOfModificationTest() {
        given(contractRepository.existsById(4L)).willReturn(true);
        ContractModification modified7 =
            new ContractModification(5L, "MODIFICATIONnnnn", 4L, studentId4, companyId4, 10, 40, 3,
                true, true, true);
        given(modificationRepository.saveAndFlush(modified7)).willReturn(modified7);
        given(modificationRepository.getOne(5L)).willReturn(contractModif7);
        Contract modified = modificationService.acceptModification(5L, false);
        assertThat(modified).isEqualTo(null);
        verify(modificationRepository).saveAndFlush(modified7);
        verify(modificationRepository).getOne(5L);
        verify(contractRepository).existsById(4L);
    }

    @Test
    public void testGetStudentInvolvedModifications() {
        given(modificationRepository.findAllByStudentId(studentId)).willReturn(
            List.of(contractModif1));
        List<ContractModification> contractModifications =
            modificationService.getStudentInvolvedModifications(studentId);
        assertThat(contractModifications).isEqualTo(List.of(contractModif1));
        verify(modificationRepository).findAllByStudentId(studentId);

    }

    @Test
    public void testGetCompanyInvolvedModifications() {
        given(modificationRepository.findAllByCompanyId(companyId)).willReturn(
            List.of(contractModif2));
        List<ContractModification> contractModifications =
            modificationService.getCompanyInvolvedModifications(companyId);
        assertThat(contractModifications).isEqualTo(List.of(contractModif2));
        verify(modificationRepository).findAllByCompanyId(companyId);
    }

    @Test
    public void testDeclineByStudent() {
        given(modificationRepository.getOne(3L)).willReturn(contractModif3);
        ContractModification declinedModification =
            new ContractModification(3L, "TERMINATION", 4L, studentId4, companyId4, 15, 60, 5, true,
                false, true);

        given(modificationRepository.saveAndFlush(declinedModification)).willReturn(
            declinedModification);
        ContractModification resultedModification =
            modificationService.declineModification(3L, false);
        assertThat(resultedModification).isEqualTo(declinedModification);
        verify(modificationRepository).getOne(3L);
        verify(modificationRepository).saveAndFlush(declinedModification);
    }

    @Test
    public void testDeclineByCompany() {
        given(modificationRepository.getOne(4L)).willReturn(contractModif4);
        ContractModification declinedModification =
            new ContractModification(4L, "EXTENSION", 4L, studentId4, companyId4, 15, 90, 5, false,
                true, true);

        given(modificationRepository.saveAndFlush(declinedModification)).willReturn(
            declinedModification);
        ContractModification resultedModification =
            modificationService.declineModification(4L, true);
        assertThat(resultedModification).isEqualTo(declinedModification);
        verify(modificationRepository).getOne(4L);
        verify(modificationRepository).saveAndFlush(declinedModification);
    }

}
