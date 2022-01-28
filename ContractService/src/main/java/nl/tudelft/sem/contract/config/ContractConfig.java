package nl.tudelft.sem.contract.config;

import java.util.List;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.repository.ContractRepository;
import nl.tudelft.sem.contract.repository.ModificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContractConfig {

    @Bean
    CommandLineRunner contactCommandLineRunner(ContractRepository contractRepository,
                                               ModificationRepository modificationRepository) {
        return args -> {
            // Creating contract and modification objects
            Contract contract1 = new Contract(15, 60, 6.5, "revirator", "amazon94", false);
            Contract contract2 = new Contract(15, 60, 6.5, "teambeurnaut", "amazon94", false);
            // Saving to db
            contractRepository.saveAll(List.of(contract1, contract2));
        };
    }
}
