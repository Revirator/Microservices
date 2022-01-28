package nl.tudelft.sem.request.test.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import nl.tudelft.sem.request.repository.StudentRequestRepository;
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
public class RepositoryTest {

    private final CompanyRequest companyRequest1;
    private final StudentRequest studentRequest;
    private final CompanyRequest companyRequest2;
    @Autowired
    private StudentRequestRepository studentRequestRepository;
    @Autowired
    private CompanyRequestRepository companyRequestRepository;

    /**
     * The constructor the test class.
     */
    public RepositoryTest() {
        this.companyRequest1 =
            new CompanyRequest(20, 400, 4, "Facebook", Arrays.asList("A", "B"));
        this.studentRequest =
            new StudentRequest(14, 56, 10, "Bogdan", Arrays.asList("A", "B"));
        this.companyRequest2 =
            new CompanyRequest(15, 450, 9, "Google", Arrays.asList("A", "B"));
        companyRequest1.setTargetStudentId("filterNetId");
        companyRequest2.setTargetStudentId("noFilter");
    }

    @Test
    @Order(1)
    public void testRequest() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);
        List<CompanyRequest> companyRequests = companyRequestRepository.findAll();
        System.out.println(companyRequests);
        assertEquals(1L, companyRequests.get(0).getServiceId());
        assertEquals(2L, companyRequests.get(1).getServiceId());

    }

    @Test
    @Order(2)
    public void testPostForRequest() {
        studentRequestRepository.saveAndFlush(studentRequest); //serviceId = 3
        StudentRequest output =
            studentRequestRepository.findByServiceId(studentRequest.getServiceId());
        assertEquals(studentRequest, output);
        assertEquals(3L, output.getServiceId());
    }

    @Test
    @Order(3)
    public void testFindRequestsForCompany() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);
        List<CompanyRequest> companyRequests =
            companyRequestRepository.findAllByCompanyId(companyRequest1.getCompanyId());
        assertFalse(companyRequests.isEmpty());
        assertThat(companyRequests).hasSameElementsAs(List.of(companyRequest1));
    }

    @Test
    @Order(4)
    public void testGetAll() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);

        List<CompanyRequest> requests = companyRequestRepository.findAll();
        assertThat(requests).isNotEmpty();
        assertThat(requests).hasSameElementsAs(List.of(companyRequest1, companyRequest2));
    }

    @Test
    @Order(5)
    public void testInvalidCompanyId() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        List<CompanyRequest> companyRequests =
            companyRequestRepository.findAllByCompanyId("Microsoft");
        assertThat(companyRequests).isEmpty();
    }

    @Test
    @Order(6)
    public void testDeleteStudentRequest() {
        studentRequestRepository.saveAndFlush(studentRequest);

        assertThat(studentRequestRepository.findAll()).hasSameElementsAs(List.of(studentRequest));
        studentRequestRepository.deleteById(studentRequest.getServiceId());
        assertThat(studentRequestRepository.findAll()).isEmpty();
    }

    @Test
    @Order(7)
    public void testFilterCompanyRequestsByHoursPerWeek() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);

        List<CompanyRequest> companyRequests =
            companyRequestRepository.findAllByHoursPerWeekBetween(16, 21);
        assertFalse(companyRequests.isEmpty());
        assertThat(companyRequests).hasSameElementsAs(List.of(companyRequest1));
        assertThat(companyRequests).doesNotContain(companyRequest2);
    }

    @Test
    @Order(8)
    public void testFilterCompanyRequestsBySalaryPerHour() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);

        List<CompanyRequest> companyRequests =
            companyRequestRepository.findAllBySalaryPerHourBetween(7, 10);
        assertFalse(companyRequests.isEmpty());
        assertThat(companyRequests).hasSameElementsAs(List.of(companyRequest2));
        assertThat(companyRequests).doesNotContain(companyRequest1);
    }


    @Test
    @Order(9)
    public void testFilterCompanyRequestsByTotalHours() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);

        List<CompanyRequest> companyRequests =
            companyRequestRepository.findAllByTotalHoursBetween(440, 460);
        assertFalse(companyRequests.isEmpty());
        assertThat(companyRequests).hasSameElementsAs(List.of(companyRequest2));
        assertThat(companyRequests).doesNotContain(companyRequest1);
    }


    @Test
    @Order(10)
    public void testFindTargetedRequests() {
        companyRequestRepository.saveAndFlush(companyRequest1);
        companyRequestRepository.saveAndFlush(companyRequest2);

        List<CompanyRequest> companyRequests =
                companyRequestRepository.findCompanyRequestsByTargetStudentId("filterNetId");

        assertFalse(companyRequests.isEmpty());
        assertThat(companyRequests).hasSameElementsAs(List.of(companyRequest1));
        assertThat(companyRequests).doesNotContain(companyRequest2);
    }
}
