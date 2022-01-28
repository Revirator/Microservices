package nl.tudelft.sem.student.config;

import java.util.List;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner studentCommandLineRunner(StudentRepository studentRepository) {
        return args -> {
            // Creating student objects
            Student s1 = new Student("revirator", "Denis Tsvetkov");
            Student s2 = new Student("BIOJECT", "Ahmet");
            Student s3 = new Student("Marin", "Marijn");
            Student s4 = new Student("teambeurnaut", "Matthijs");
            // Saving to db
            studentRepository.saveAll(List.of(s1, s2, s3, s4));
        };
    }
}
