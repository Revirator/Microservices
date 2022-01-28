package nl.tudelft.sem.student.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import nl.tudelft.sem.student.repository.StudentRepository;
import nl.tudelft.sem.student.valueobjects.Company;
import nl.tudelft.sem.student.valueobjects.Feedback;
import nl.tudelft.sem.student.valueobjects.FeedbackResponse;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class StudentServiceFeedback extends StudentService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new GsonBuilder().create();

    public StudentServiceFeedback(RestTemplate restTemplate, StudentRepository studentRepository) {
        super(restTemplate, studentRepository);
    }

    /**
     * Receives a request from the controller and forwards it to
     * the Feedback and Company microservices.
     *
     * @param companyId the ID for the company for which we want to get the feedbacks
     * @return a List of all the feedbacks the company has received including the new one
     */
    public FeedbackResponse getFeedbackForCompany(String companyId) {
        List<Feedback> feedbacks = new ArrayList<>();
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://FEEDBACK-SERVICE/feedback/" + companyId, List.class);
        double averageRating = 0;
        assert response != null;
        for (LinkedHashMap o : response) {
            Feedback f = mapper.convertValue(o, Feedback.class);
            averageRating += f.getStarRating();
            feedbacks.add(f);
        }
        averageRating /= feedbacks.size();
        Company company =
            restTemplate.getForObject("http://COMPANY-SERVICE/company/" + companyId + "/",
                Company.class);
        company.setAverageRating(Math.max(0, averageRating));
        FeedbackResponse vo = new FeedbackResponse();
        vo.setCompany(company);
        vo.setFeedbacks(feedbacks);
        return vo;
    }

    /**
     * Receives a request from the controller and forwards it to the Feedback microservice.
     * Also invokes the getFeedbackForCompany to return a List of all feedbacks.
     *
     * @param companyId the ID for the company for which we want to post the feedback
     * @param feedback  new feedback to be posted for that company
     * @return a List of all the feedbacks the company has received including the new one
     */
    public FeedbackResponse postFeedbackForCompany(String companyId, Feedback feedback) {
        String feedbackJsonObject = gson.toJson(feedback);
        HttpEntity<String> request = requestCreator(feedbackJsonObject);
        restTemplate.postForObject("http://FEEDBACK-SERVICE/feedback/" + companyId, request,
            Feedback.class);
        return getFeedbackForCompany(companyId);
    }

}
