package nl.tudelft.sem.feedback.tests.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.feedback.controller.FeedbackController;
import nl.tudelft.sem.feedback.entity.Feedback;
import nl.tudelft.sem.feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class FeedbackControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final String userId = "Denis";
    private final String payload = "{\n"
            + "    \"starRating\": 5,\n"
            + "    \"description\": \"Wow! Random feedback...\"\n"
            + "}";
    private final String url = "/feedback/{userId}";
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private FeedbackService feedbackService;

    @BeforeEach
    public void setup() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(new FeedbackController(feedbackService)).build();
    }

    /**
     * Tests the get request that returns all feedbacks in the database.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getAll()</code> in FeedbackService is invoked.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/feedback/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(feedbackService).getAll();
    }

    /**
     * Tests the get request that returns all feedbacks in the database for a specific user.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getFeedbackForUser()</code> in FeedbackService
     * is invoked for the given userId.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetFeedbackForUser() throws Exception {
        mockMvc.perform(get(url, userId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(feedbackService).getFeedbackForUser(userId);
    }

    /**
     * Tests the post request for a feedback.
     * Verifies that the payload is correct (and does not return status code 415).
     * Verifies that the request returns status code 200.
     * Verifies that <code>postFeedbackForUser()</code> in the feedbackService.
     * for the given feedback and userId is invoked.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testPostFeedbackForUser() throws Exception {
        mockMvc.perform(post(url, userId).content(payload)
            .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        Feedback feedback = gson.fromJson(payload, Feedback.class);
        verify(feedbackService).postFeedbackForUser(feedback, userId);
    }

    @Test
    public void testInvalidFeedback() throws Exception {
        mockMvc.perform(post(url, userId)
                        .content(payload.replace("5", "6"))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
        mockMvc.perform(post(url, userId)
                        .content(payload.replace("5", "0"))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}
