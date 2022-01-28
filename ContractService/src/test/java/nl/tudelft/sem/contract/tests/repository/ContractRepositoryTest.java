package nl.tudelft.sem.contract.tests.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.repository.ContractRepository;
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
class ContractRepositoryTest {

    private final Contract c1;
    private final Contract c2;
    private final Contract c3;
    private final Contract c4;
    @Autowired
    private ContractRepository contractRepository;

    /**
     * Generating three contracts to be tested.
     */
    public ContractRepositoryTest() {
        c1 = new Contract(351L, 4, 16, 12.5, "student1", "company1", false);
        c2 = new Contract(23513L, 2, 12, 13.5, "student2", "company2", false);
        c3 = new Contract(3L, 3, 12, 14.5, "student3", "company3", false);
        c4 = new Contract(4L, 3, 12, 14.5, "student4", "company4", false);
    }

    /**
     * Tests whether the ordering of putting contracts to repository works by checking if
     * contractID's are updated sequentially.
     */
    @Test
    @Order(1)
    public void testContractSequenceGenerator() {
        contractRepository.saveAndFlush(c1);
        contractRepository.saveAndFlush(c2);
        List<Contract> contracts = contractRepository.findAll();
        assertEquals(1, contracts.get(0).getContractId());
        assertEquals(2, contracts.get(1).getContractId());
        assertTrue(contractRepository.existsById(1L));
        assertTrue(contractRepository.existsById(2L));
    }

    /**
     * Tests whether a user sending a POST request to generate a new contract works.
     */
    @Test
    @Order(2)
    public void testPostContract() {
        contractRepository.saveAndFlush(c3);
        Contract contract = contractRepository.getOne(3L);
        assertEquals(c3, contract);
        assertEquals(3, contract.getContractId());
    }

    /**
     * Tests whether getting a contract by its ID works.
     */
    @Test
    @Order(3)
    public void testGetContractById() {
        contractRepository.saveAndFlush(c4);
        Optional<Contract> expected =
            Optional.of(new Contract(4L, 3, 12, 14.5, "student4", "company4", false));
        Optional<Contract> actual = contractRepository.findById(4L);
        assertEquals(expected, actual);
    }
}