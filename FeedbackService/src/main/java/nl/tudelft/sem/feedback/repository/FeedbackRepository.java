package nl.tudelft.sem.feedback.repository;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findAllByUserId(String userId);
}
