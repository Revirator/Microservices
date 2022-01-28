package nl.tudelft.sem.company.tests.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.repository.CompanyRepository;
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
public class CompanyRepositoryTest {
    private final Company c1;
    private final Company c2;
    private final Company c3;
    @Autowired
    private CompanyRepository companyRepository;

    /**
     * Generate three companies to be tested.
     */
    public CompanyRepositoryTest() {
        c1 = new Company("TUD", "TuDelft22", 3.5);
        c2 = new Company("AMZ", "Amazon", 1.88);
        c3 = new Company("PQ", "Quatric", 4.64);
    }

    @Test
    @Order(1)
    public void testCompanySequenceGenerator() {
        companyRepository.save(c1); //username = "TUD"
        companyRepository.saveAndFlush(c2); //username = "AMZ"
        List<Company> companies = companyRepository.findAll();
        assertEquals("TUD", companies.get(0).getUsername());
        assertEquals("AMZ", companies.get(1).getUsername());
        assertTrue(companyRepository.existsById("TUD"));
        assertTrue(companyRepository.existsById("AMZ"));
    }

    @Test
    @Order(2)
    public void testPostCompany() {
        companyRepository.saveAndFlush(c3); //username = "PQ"
        Company output = companyRepository.getOne(c3.getUsername());
        assertEquals(c3, output);
        assertEquals("PQ", output.getUsername());
    }

    @Test
    @Order(3)
    public void testGetAllCompanies() {
        companyRepository.saveAndFlush(c1); //username = "TUD"
        companyRepository.saveAndFlush(c2); //username = "AMZ"
        companyRepository.saveAndFlush(c3); //username = "PQ"
        List<Company> companies = companyRepository.findAll();
        assertThat(companies).isNotEmpty();
        assertThat(companies).hasSameElementsAs(List.of(c1, c2, c3));
    }
}
