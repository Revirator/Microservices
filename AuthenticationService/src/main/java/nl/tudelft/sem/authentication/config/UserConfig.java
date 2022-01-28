package nl.tudelft.sem.authentication.config;

import java.util.List;
import nl.tudelft.sem.authentication.entity.User;
import nl.tudelft.sem.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserConfig {

    private final String student = "ROLE_STUDENT";
    private final String company = "ROLE_COMPANY";
    @Autowired
    private PasswordEncoder encoder;

    @Bean
    CommandLineRunner userCommandLineRunner(UserRepository userRepository) {
        return args -> {
            User a1 = new User("admin", encoder.encode("admin"), "ROLE_ADMIN", "admin");
            User s1 = new User("revirator", encoder.encode("1234"), student, "Denis");
            User s2 = new User("BIOJECT", encoder.encode("4321"), student, "Ahmet");
            User s3 = new User("Marin", encoder.encode("5678"), student, "Marijn");
            User s4 = new User("teambeurnaut", encoder.encode("8765"), student, "Matthijs");
            User c1 = new User("amazon94", encoder.encode("jeff"), company, "Amazon");
            User c2 = new User("tud1842", encoder.encode("tud"), company, "TU Delft");
            userRepository.saveAll(List.of(a1, s1, s2, s3, s4, c1, c2));
        };
    }
}
