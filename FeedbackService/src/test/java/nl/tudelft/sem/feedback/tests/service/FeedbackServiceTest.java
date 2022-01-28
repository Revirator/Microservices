package nl.tudelft.sem.feedback.tests.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.repository.FeedbackRepository;
import nl.tudelft.sem.feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    private final String userId = "Denis";
    private final Feedback f1 = new Feedback(1L, 5, "WOW! Good job.", null);
    private final Feedback f2 = new Feedback(3L, 1, null, userId);
    @Mock
    private FeedbackRepository feedbackRepository;
    private FeedbackService feedbackService;

    @BeforeEach
    public void setup() {
        feedbackService = new FeedbackService(feedbackRepository);
    }

    @Test
    public void testGetAll() {
        given(feedbackRepository.findAll()).willReturn(List.of(f1, f2));
        List<Feedback> result = feedbackService.getAll();
        assertThat(result).hasSameElementsAs(List.of(f1, f2));
        verify(feedbackRepository).findAll();
    }

    @Test
    public void testGetFeedbackForUser() {
        given(feedbackRepository.findAllByUserId(userId)).willReturn(List.of(f1, f2));
        List<Feedback> result = feedbackService.getFeedbackForUser(userId);
        assertThat(result).hasSameElementsAs(List.of(f1, f2));
        verify(feedbackRepository).findAllByUserId(userId);
    }

    @Test
    public void testPostFeedbackForUser() {
        given(feedbackRepository.save(f1)).willReturn(f1);
        Feedback result = feedbackService.postFeedbackForUser(f1, userId);
        assertThat(result.getUserId()).isNotNull();
        assertEquals(result.getFeedbackId(), 1);
        verify(feedbackRepository).save(f1);
    }
}
