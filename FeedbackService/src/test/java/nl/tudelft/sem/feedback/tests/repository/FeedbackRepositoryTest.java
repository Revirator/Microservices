package nl.tudelft.sem.feedback.tests.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// DON'T RUN THE TESTS SEPARATELY!
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FeedbackRepositoryTest {

    private final Feedback f1;
    private final Feedback f2;
    private final Feedback f3;
    @Autowired
    private FeedbackRepository feedbackRepository;

    /**
     * Generating three feedbacks to be tested.
     */
    public FeedbackRepositoryTest() {
        f1 = new Feedback(5, "WOW! Good job.", "Denis");
        f2 = new Feedback(3, "Decent job!", "Ismael");
        f3 = new Feedback(1, null, "Denis");
    }

    @Test
    @Order(1)
    public void testFeedbackSequenceGenerator() {
        feedbackRepository.saveAndFlush(f1); //feedbackId = 1
        feedbackRepository.saveAndFlush(f2); //feedbackId = 2
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertEquals(1, feedbacks.get(0).getFeedbackId());
        assertEquals(2, feedbacks.get(1).getFeedbackId());
        assertTrue(feedbackRepository.existsById(1L));
        assertTrue(feedbackRepository.existsById(2L));
    }

    @Test
    @Order(2)
    public void testPostFeedbackForUser() {
        feedbackRepository.saveAndFlush(f3); //feedbackId = 3
        Feedback output = feedbackRepository.getOne(f3.getFeedbackId());
        assertEquals(f3, output);
        assertEquals(3, output.getFeedbackId());
    }

    @Test
    @Order(3)
    public void testGetFeedbackForUser() {
        feedbackRepository.saveAndFlush(f1); //feedbackId = 4
        feedbackRepository.saveAndFlush(f2); //feedbackId = 5
        feedbackRepository.saveAndFlush(f3); //feedbackId = 6
        List<Feedback> feedbacks = feedbackRepository.findAllByUserId(f1.getUserId());
        assertThat(feedbacks).isNotEmpty();
        assertThat(feedbacks).hasSameElementsAs(List.of(f1, f3));
    }

    @Test
    @Order(4)
    public void testGetAll() {
        feedbackRepository.saveAndFlush(f1); //feedbackId = 7
        feedbackRepository.saveAndFlush(f2); //feedbackId = 8
        feedbackRepository.saveAndFlush(f3); //feedbackId = 9
        List<Feedback> feedbacks = feedbackRepository.findAll();
        assertThat(feedbacks).isNotEmpty();
        assertThat(feedbacks).hasSameElementsAs(List.of(f1, f3, f2));
    }

    @Test
    @Order(5)
    public void testInvalidUserId() {
        feedbackRepository.saveAndFlush(f1); //feedbackId = 10
        List<Feedback> feedbacks = feedbackRepository.findAllByUserId(f2.getUserId());
        assertThat(feedbacks).isEmpty();
    }
}
