package nl.tudelft.sem.contract.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.repository.ContractRepository;
import nl.tudelft.sem.contract.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
class ContractServiceTest {

    private final Contract c1 = new Contract(1L, 4, 16, 12.5, "student1", "company1", false);
    private final Contract c2 = new Contract(2L, 2, 12, 13.5, "student2", "company2", false);
    private final Contract c3 = new Contract(3L, 1, 12, 13.5, "student1", "company3", false);
    private final Contract c4 = new Contract(3L, 6, 12, 13.5, "student3", "company1", false);
    private final Contract wrongDurationContract =
        new Contract(3L, 6, 154, 13.5, "student3", "company1", false);
    private final Contract wrongHoursPerWeekContract =
        new Contract(3L, 21, 154, 13.5, "student3", "company1", false);
    @Mock
    private ContractRepository contractRepository;
    private ContractService contractService;

    @BeforeEach
    public void setup() {
        contractService = new ContractService(contractRepository);
    }

    /**
     * Tests getAllContracts() method to get all contracts.
     */
    @Test
    public void testGetAllContracts() {
        given(contractRepository.findAll()).willReturn(List.of(c1, c2));
        List<Contract> result = contractService.findAllContracts();
        assertThat(result).hasSameElementsAs(List.of(c1, c2));
        verify(contractRepository).findAll();
    }

    /**
     * Tests getContractByID() method to get a contract by its contractID.
     */
    @Test
    public void testGetContractById() {
        given(contractRepository.findById(1L)).willReturn(Optional.of(c1));
        Optional<Contract> contract = contractService.getContractById(1L);
        assertThat(contract).isEqualTo(Optional.of(c1));
        verify(contractRepository).findById(1L);
    }

    /**
     * Tests save() method to save a contract to the repo.
     */
    @Test
    public void testSaveContract() {
        given(contractRepository.save(c2)).willReturn(c2);
        Contract contract = contractService.saveContract(c2);
        assertThat(contract).isEqualTo(c2);
        verify(contractRepository).save(c2);

    }

    @Test
    public void testWrongDurationContract() {
        Contract contract = contractService.saveContract(wrongDurationContract);
        assertThat(contract).isEqualTo(null);
        verify(contractRepository, Mockito.times(0)).save(wrongDurationContract);

    }

    @Test
    public void testWrongHoursPerWeekContract() {
        Contract contract = contractService.saveContract(wrongHoursPerWeekContract);
        assertThat(contract).isEqualTo(null);
        verify(contractRepository, Mockito.times(0)).save(wrongHoursPerWeekContract);

    }

    @Test
    public void testGetContractsByStudentId() {
        String studentId = "student1";
        given(contractRepository.findAllByStudentId(studentId)).willReturn(List.of(c1, c3));
        List<Contract> contracts = contractService.findContractsByStudentId(studentId);
        assertThat(contracts).hasSameElementsAs(List.of(c1, c3));
        verify(contractRepository).findAllByStudentId(studentId);
    }

    @Test
    public void testGetContractsByCompanyId() {
        String companyId = "company1";
        given(contractRepository.findAllByCompanyId(companyId)).willReturn(List.of(c1, c4));
        List<Contract> contracts = contractService.findContractsByCompanyId(companyId);
        assertThat(contracts).hasSameElementsAs(List.of(c1, c4));
        verify(contractRepository).findAllByCompanyId(companyId);
    }

}
