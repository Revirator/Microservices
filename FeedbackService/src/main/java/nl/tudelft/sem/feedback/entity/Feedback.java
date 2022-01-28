package nl.tudelft.sem.feedback.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long feedbackId;
    private int starRating;
    private String description;
    private String userId;

    /**
     * Constructor for a feedback without an id.
     * Used for testing purposes only.
     *
     * @param starRating  rating between 1 and 5
     * @param description text description of the feedback
     * @param userId      the NetID/username of the student/company
     */
    public Feedback(int starRating, String description, String userId) {
        this.starRating = starRating;
        this.description = description;
        this.userId = userId;
    }
}
