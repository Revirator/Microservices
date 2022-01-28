package nl.tudelft.sem.company.config;

import java.util.List;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.repository.CompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(CompanyRepository companyRepository) {
        return args -> {
            Company c1 = new Company("amazon94", "Amazon", 4.9);
            Company c2 = new Company("tud1842", "TU Delft", 4.2);
            companyRepository.saveAll(List.of(c1, c2));
        };
    }
}
