package nl.tudelft.sem.student.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    private Long feedbackId;
    private int starRating;
    private String description;
    private String userId;

    /**
     * Constructor for a feedback without an id.
     * Used for testing purposes only.
     *
     * @param starRating rating between 1 and 5
     * @param description text description of the feedback
     */
    public Feedback(int starRating, String description) {
        this.starRating = starRating;
        this.description = description;
    }
}
