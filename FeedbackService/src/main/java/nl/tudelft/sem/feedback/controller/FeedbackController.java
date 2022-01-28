package nl.tudelft.sem.feedback.controller;

import java.util.List;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * Purely for testing purposes.
     * Returns all feedbacks in the database.
     *
     * @return a list of all the feedbacks
     */
    @GetMapping("/")
    @ResponseBody
    public List<Feedback> getAll() {
        return feedbackService.getAll();
    }

    /**
     * Receives the data for a feedback that needs to be saved to the database.
     * Forwards the request to the feedback service class.
     *
     * @param userId   the username of the User
     * @param feedback the star rating + optional description
     * @return new Feedback instance with feedbackId and userId
     */
    @PostMapping("/{userId}")
    @ResponseBody
    public ResponseEntity<Feedback> postFeedbackForUser(@PathVariable String userId,
                                        @RequestBody Feedback feedback) {
        if (feedback.getStarRating() < 1 || feedback.getStarRating() > 5) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(feedbackService.postFeedbackForUser(feedback, userId));
    }

    /**
     * Handles the request asking for the feedbacks for a specific user.
     * Forwards the request to the feedback service class.
     *
     * @param userId the username of the User
     * @return List of feedback for that user (empty list if no feedbacks)
     */
    @GetMapping("/{userId}")
    @ResponseBody
    public List<Feedback> getFeedbackForUser(@PathVariable String userId) {
        return feedbackService.getFeedbackForUser(userId);
    }
}
