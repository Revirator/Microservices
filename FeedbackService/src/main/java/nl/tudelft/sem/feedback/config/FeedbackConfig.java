package nl.tudelft.sem.feedback.config;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.repository.FeedbackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedbackConfig {

    @Bean
    CommandLineRunner userCommandLineRunner(FeedbackRepository feedbackRepository) {
        return args -> {
            Feedback f1 = new Feedback(1L, 1, "Wow! Random feedback...", "revirator");
            Feedback f2 = new Feedback(2L, 2, "ldbajflgqfoqq", "BIOJECT");
            Feedback f3 = new Feedback(3L, 3, "", "Marin");
            Feedback f4 = new Feedback(4L, 4, null, "teambeurnaut");
            Feedback f5 = new Feedback(5L, 4, "<insert feedback>", "revirator");
            Feedback f6 = new Feedback(6L, 1, "Terrible work conditions...", "amazon94");
            Feedback f7 = new Feedback(7L, 2, "Technical difficulties all the time ;/", "tud1842");
            feedbackRepository.saveAll(List.of(f1, f2, f3, f4, f5, f6, f7));
        };
    }
}
