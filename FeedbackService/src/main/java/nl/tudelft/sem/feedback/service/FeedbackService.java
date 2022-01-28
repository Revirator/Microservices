package nl.tudelft.sem.feedback.service;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @return a List of all the feedbacks in the database
     */
    public List<Feedback> getAll() {
        return feedbackRepository.findAll();
    }

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @param feedback the new feedback for the user
     * @param userId the username of the user
     * @return new Feedback instance with a feedbackId and userId
     */
    public Feedback postFeedbackForUser(Feedback feedback, String userId) {
        feedback.setUserId(userId);
        return feedbackRepository.save(feedback);
    }

    /**
     * Receives the forwarded request from the controller class
     * and forwards it down the chain to the repository interface.
     *
     * @param userId the username of the user
     * @return List of feedbacks for that user (empty list if no feedbacks)
     */
    public List<Feedback> getFeedbackForUser(String userId) {
        return feedbackRepository.findAllByUserId(userId);
    }
}
