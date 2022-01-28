package nl.tudelft.sem.student.controller;

import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
import nl.tudelft.sem.student.valueobjects.Feedback;
import nl.tudelft.sem.student.valueobjects.FeedbackResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Slf4j
public class StudentControllerFeedback extends StudentController {

    public StudentControllerFeedback(StudentService studentService,
                                     StudentServiceContract studentServiceContract,
                                     StudentServiceFeedback studentServiceFeedback,
                                     StudentServiceRequest studentServiceRequest) {
        super(studentService, studentServiceContract, studentServiceFeedback,
                studentServiceRequest);
    }


    /**
     * Sends a request to the Feedback Microservice to get the feedbacks for a company.
     *
     * @param studentId the ID of the student making the request
     * @param companyId the ID for the company for which we want the feedbacks
     * @return a List of all the feedbacks the company has received
     */
    @GetMapping("/{studentId}/feedback/{companyId}")
    @ResponseBody
    public FeedbackResponse getFeedbackForCompany(@PathVariable("studentId") String studentId,
                                                  @PathVariable("companyId") String companyId) {
        log.info(studentId + " requests feedback for " + companyId);
        return super.studentServiceFeedback.getFeedbackForCompany(companyId);
    }

    /**
     * Sends a request to the Feedback Microservice to post a new feedback for a company.
     *
     * @param studentId the ID of the student making the request
     * @param companyId the ID for the company for which we want to post the feedback
     * @param feedback  the new feedback to be posted for that company
     * @return a List of all the feedbacks the company has received including the new one
     */
    @PostMapping("/{studentId}/feedback/{companyId}")
    @ResponseBody
    public FeedbackResponse postFeedbackForCompany(@PathVariable String studentId,
                                                   @PathVariable String companyId,
                                                   @RequestBody Feedback feedback) {
        log.info(studentId + " posts new feedback for " + companyId);
        return studentServiceFeedback.postFeedbackForCompany(companyId, feedback);
    }

}
