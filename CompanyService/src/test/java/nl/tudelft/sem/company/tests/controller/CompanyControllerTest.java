package nl.tudelft.sem.company.tests.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.company.controller.CompanyController;
import nl.tudelft.sem.company.entity.Company;
import nl.tudelft.sem.company.service.CompanyService;
import nl.tudelft.sem.company.valueobjects.CompanyRequest;
import nl.tudelft.sem.company.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.company.valueobjects.ContractModification;
import nl.tudelft.sem.company.valueobjects.Feedback;
import nl.tudelft.sem.company.valueobjects.FilterTag;
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
public class CompanyControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final String expertise = "C++";
    private final String companyId = "Microsoft";
    private final Long serviceId = 2L;
    private final String studentId = "Ismael";
    private final String payload =
        "{\n" + "    \"username\": \"amazON94\",\n" + "    \"name\": \"Amazon\",\n"
            + "    \"averageRating\": 4.5\n" + "}";
    private final Long modificationId = 1L;
    private final CompanyRequest companyRequest = new CompanyRequest(1L, 10, 20,
            14, "companyId", List.of("requirements"), new ArrayList<>(), false, "studentId", "");

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CompanyService companyService;

    @BeforeEach
    public void setup() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(new CompanyController(companyService)).build();
    }

    /**
     * Tests the get request that returns all companies in the database.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getAll()</code> in CompanyService is invoked.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/company/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyService).getAll();
    }

    /**
     * Tests the get request that returns the Company in the database for a specific companyId.
     * Verifies that the request returns status code 200.
     * Verifies that <code>GetCompanyByUsername()</code> in CompanyService
     * is invoked for the given username.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetCompanyByCompanyId() throws Exception {
        mockMvc.perform(get("/company/{companyId}/", companyId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyService).findCompanyByUsername(companyId);
    }

    /**
     * Tests the post request that posts and returns the Company that is posted.
     * Verifies that the request returns status code 200.
     * Verifies that <code>PostCompany()</code> in CompanyService
     * is invoked for the given company.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testPostCompany() throws Exception {
        mockMvc.perform(post("/company/").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        Company company = gson.fromJson(payload, Company.class);
        verify(companyService).saveCompany(company);
    }

    /**
     * Tests the get request that returns all feedbacks in the database for a specific username.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getFeedbackForStudent()</code> in FeedbackService
     * is invoked for the given username.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetFeedbackForStudent() throws Exception {
        // "test" is just a dummy company ID
        mockMvc.perform(get("/company/test/feedback/{studentId}", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).getFeedbackForStudent(studentId);
    }

    /**
     * Tests the post request for a feedback for student.
     * Verifies that the payload is correct (and does not return status code 415).
     * Verifies that the request returns status code 200.
     * Verifies that <code>postFeedbackForStudent()</code> in the CompanyService
     * for the given feedback and username is invoked.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testPostFeedbackForStudent() throws Exception {
        String feedbackPayload = "{\n" + "    \"starRating\": 1,\n"
            + "    \"description\": \"Wow! Random feedback...\"\n" + "}";
        // "test" is just a dummy company ID
        mockMvc.perform(
                post("/company/test/feedback/{studentId}", studentId).content(feedbackPayload)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        Feedback feedback = gson.fromJson(feedbackPayload, Feedback.class);
        verify(companyService).postFeedbackForStudent(studentId, feedback);
    }

    @Test
    public void testGetContractsByCompanyId() throws Exception {
        mockMvc.perform(get("/company/{companyId}/contracts/", companyId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).getContractsByCompanyId(companyId);
    }

    @Test
    public void testAcceptByCompany() throws Exception {
        String companyId = "Microsoft";
        String json = "";
        Long serviceId = 1L;
        mockMvc.perform(post("/company/{companyId}/request/acceptOwn/{serviceId}/{studentId}",
            companyId,
                serviceId, studentId).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).acceptByCompany(companyId, serviceId, studentId);
    }

    @Test
    public void testAcceptStudentRequest() throws Exception {
        String companyId = "Microsoft";
        String json = "";
        Long serviceId = 1L;
        mockMvc.perform(
                post("/company/{companyId}/request/accept/{serviceId}", companyId, serviceId)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyService).acceptStudentRequest(companyId, serviceId);

    }

    /**
     * Tests the GetRequest that returns all Students in the database for a specific expertise.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getAllStudentRequests()</code> in RequestService
     * is invoked for the given expertise.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testSearchStudents() throws Exception {
        // "test" is just a dummy company ID
        mockMvc.perform(get("/company/test/search/{expertise}", expertise))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).searchExpertiseStudents(expertise);
    }

    @Test
    public void testProposeModification() throws Exception {
        String modificationPayload = "{\n" + "        \"modificationType\": \"MODIFICATION\",\n"
            + "        \"studentID\": \"2351353\",\n" + "        \"companyID\": \"1351351\",\n"
            + "        \"hoursPerWeek\": 4,\n" + "        \"totalHours\": 16,\n"
            + "        \"pricePerHour\": 12.5,\n" + "        \"acceptedByCompany\": true,\n"
            + "        \"acceptedByStudent\": false,\n" + "        \"finished\": false\n" + "}";
        mockMvc.perform(post("/company/{companyId}/modification/proposes", companyId).content(
                modificationPayload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        ContractModification modification =
            gson.fromJson(modificationPayload, ContractModification.class);
        verify(companyService).proposeModification(modification);
    }

    @Test
    public void testAcceptModification() throws Exception {
        mockMvc.perform(
                post("/company/{companyId}/modification/accepts/{modificationId}", companyId,
                    modificationId)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyService).acceptModification(companyId, modificationId);
    }

    @Test
    public void testDeclineModification() throws Exception {
        mockMvc.perform(
                post("/company/{companyId}/modification/declines/{modificationId}", companyId,
                    modificationId)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyService).declineModification(companyId, modificationId);
    }

    @Test
    public void testGetProposedModifications() throws Exception {
        mockMvc.perform(get("/company/{companyId}/modification/proposed", companyId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).getModificationsToRespond(companyId);
    }

    @Test
    public void testGetAllInvolvedInModifications() throws Exception {
        mockMvc.perform(get("/company/{companyId}/modification/all", companyId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).getAllInvolvedInModifications(companyId);
    }

    @Test
    public void testDeleteAllCompanyRequests() throws Exception {
        mockMvc.perform(delete("/company/{companyId}/deleteAll", companyId))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).deleteAllCompanyRequests(companyId);
    }

    @Test
    public void testDeleteCompanyRequest() throws Exception {
        mockMvc.perform(delete("/company/{companyId}/delete/{serviceId}", companyId, serviceId))
        .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).deleteCompanyRequest(companyId, serviceId);
    }

    @Test
    public void testUpdateCompanyRequest() throws Exception {
        String payload = "{\n    \"salaryPerHour\": 20\n}";
        mockMvc.perform(put("/company/{companyId}/update/{serviceId}", companyId, serviceId)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        CompanyRequestChange changeData = gson.fromJson(payload, CompanyRequestChange.class);
        verify(companyService).updateCompanyRequest(companyId, serviceId, changeData);
    }

    @Test
    public void testPostTargetedCompanyRequest() throws Exception {
        String companyRequestJsonObject = gson.toJson(companyRequest);
        String targetedStudentId = "target";
        mockMvc.perform(
                        post("/company/companyId/request/targeted/{studentId}", targetedStudentId)
                                .content(companyRequestJsonObject)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(companyService).postTargetedCompanyRequest(companyRequest, targetedStudentId);
    }

    @Test
    public void testSearchAvailableStudents() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"hoursPerWeek\",\n"
                + "    \"startOfTheInterval\": 8,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/company/companyId/search/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyService).searchAvailableStudents(resultFilter);
    }

    @Test
    public void testCompanyAcceptsChangesToCompanyRequest() throws Exception {
        mockMvc.perform(post("/company/{companyId}/request/"
                + "acceptChanges/{modificationId}", companyId, 1L))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).acceptSuggestedChanges(companyId, 1L);
    }

    @Test
    public void testCompanyRejectsChangesToCompanyRequest() throws Exception {
        mockMvc.perform(post("/company/{companyId}/request/"
                        + "rejectChanges/{modificationId}", companyId, 2L))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).rejectSuggestedChanges(companyId, 2L);
    }

    @Test
    public void testGetAllModificationsForCompany() throws Exception {
        mockMvc.perform(get("/company/{companyId}/request/"
                        + "modifications/all", companyId))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyService).getAllJobModifications(companyId);
    }
}
