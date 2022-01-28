package nl.tudelft.sem.company.valueobjects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Student student;
    private List<Feedback> feedbacks;
}
