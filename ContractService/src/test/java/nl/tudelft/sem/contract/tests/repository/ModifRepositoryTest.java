package nl.tudelft.sem.contract.tests.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.contract.entity.ContractModification;
import nl.tudelft.sem.contract.repository.ModificationRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ModifRepositoryTest {
    ContractModification contractModif1;
    ContractModification contractModif2;
    ContractModification contractModif3;
    ContractModification contractModif4;

    @Autowired
    private ModificationRepository modificationRepository;

    /**
     * Generating three contracts to be tested.
     */
    public ModifRepositoryTest() {
        contractModif1 =
            new ContractModification("MODIFICATION", 1L, "student1", "company1", 15, 60, 5, true,
                false, false);
        contractModif2 =
            new ContractModification("MODIFICATION", 2L, "student2", "company2", 15, 60, 5, false,
                true, false);
        contractModif3 =
            new ContractModification("TERMINATION", 3L, "student3", "company3", 0, 0, 0, true,
                false, false);
        contractModif4 =
            new ContractModification("TERMINATION", 4L, "student4", "company4", 0, 0, 0, false,
                true, false);
    }

    @Test
    @Order(1)
    public void testModificationSequenceGenerator() {
        modificationRepository.saveAndFlush(contractModif1);
        modificationRepository.saveAndFlush(contractModif2);
        List<ContractModification> contractModifications = modificationRepository.findAll();
        assertEquals(1L, contractModifications.get(0).getModificationId());
        assertEquals(2L, contractModifications.get(1).getModificationId());
        assertTrue(modificationRepository.existsById(1L));
        assertTrue(modificationRepository.existsById(2L));
    }

    @Test
    @Order(2)
    public void testPostContract() {
        modificationRepository.saveAndFlush(contractModif3);
        List<ContractModification> contractModification =
            modificationRepository.findAllByStudentIdAndAcceptedByCompanyAndFinished("student3",
                true, false);
        assertEquals(contractModification.get(0), contractModif3);
        assertEquals(3L, contractModif3.getModificationId());
    }

    @Test
    @Order(3)
    public void testGetModificationByCompany() {
        modificationRepository.saveAndFlush(contractModif1);
        modificationRepository.saveAndFlush(contractModif2);
        modificationRepository.saveAndFlush(contractModif3);
        modificationRepository.saveAndFlush(contractModif4);

        List<ContractModification> contractModificationByCompany =
            modificationRepository.findAllByStudentIdAndAcceptedByCompanyAndFinished("student1",
                true, false);
        assertEquals(contractModificationByCompany.get(0), contractModif1);
        assertFalse(contractModificationByCompany.contains(contractModif2));
        assertFalse(contractModificationByCompany.contains(contractModif3));
        assertFalse(contractModificationByCompany.contains(contractModif4));
    }

    @Test
    @Order(4)
    public void testGetModificationByStudent() {
        modificationRepository.saveAndFlush(contractModif1);
        modificationRepository.saveAndFlush(contractModif2);
        modificationRepository.saveAndFlush(contractModif3);
        modificationRepository.saveAndFlush(contractModif4);

        List<ContractModification> contractModificationByStudent =
            modificationRepository.findAllByCompanyIdAndAcceptedByStudentAndFinished("company2",
                true, false);
        assertEquals(contractModificationByStudent.get(0), contractModif2);
        assertFalse(contractModificationByStudent.contains(contractModif1));
        assertFalse(contractModificationByStudent.contains(contractModif3));
        assertFalse(contractModificationByStudent.contains(contractModif4));
    }

    @Test
    @Order(5)
    public void testGetCompanyInvolvedModifications() {
        modificationRepository.saveAndFlush(contractModif1);
        modificationRepository.saveAndFlush(contractModif2);
        modificationRepository.saveAndFlush(contractModif3);
        modificationRepository.saveAndFlush(contractModif4);
        List<ContractModification> contractModifications =
            modificationRepository.findAllByCompanyId("company2");
        assertEquals(contractModifications.get(0), contractModif2);
        assertFalse(contractModifications.contains(contractModif1));
        assertFalse(contractModifications.contains(contractModif3));
        assertFalse(contractModifications.contains(contractModif4));
    }

    @Test
    @Order(6)
    public void testGetStudentInvolvedModifications() {
        modificationRepository.saveAndFlush(contractModif1);
        modificationRepository.saveAndFlush(contractModif2);
        modificationRepository.saveAndFlush(contractModif3);
        modificationRepository.saveAndFlush(contractModif4);

        List<ContractModification> contractModifications =
            modificationRepository.findAllByStudentId("student1");
        assertEquals(contractModifications.get(0), contractModif1);
        assertFalse(contractModifications.contains(contractModif2));
        assertFalse(contractModifications.contains(contractModif3));
        assertFalse(contractModifications.contains(contractModif4));
    }
}
