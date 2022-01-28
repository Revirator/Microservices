package nl.tudelft.sem.request.config;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import nl.tudelft.sem.request.repository.StudentRequestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(CompanyRequestRepository companyRequestRepository,
                                            StudentRequestRepository studentRequestRepository) {
        return args -> {
            StudentRequest studentRequest1 =
                new StudentRequest(1L, 10, 60, 11.8, "Bogdan", List.of("C" + "++", "Java"));
            StudentRequest studentRequest2 =
                new StudentRequest(2L, 8, 256, 15.4, "Mihai", List.of("C"));
            CompanyRequest companyRequest1 =
                new CompanyRequest(3L, 20, 400, 4.8, "amazon94", Arrays.asList("Python", "Scala"));
            CompanyRequest companyRequest2 =
                new CompanyRequest(4L, 16, 300, 5.5, "tud1842", Arrays.asList("C++", "Java"));
            studentRequestRepository.saveAll(List.of(studentRequest1, studentRequest2));
            companyRequestRepository.saveAll(List.of(companyRequest1, companyRequest2));
        };
    }
}
