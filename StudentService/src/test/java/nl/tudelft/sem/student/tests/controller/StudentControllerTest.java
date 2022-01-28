package nl.tudelft.sem.student.tests.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.student.controller.StudentController;
import nl.tudelft.sem.student.controller.StudentControllerContract;
import nl.tudelft.sem.student.controller.StudentControllerFeedback;
import nl.tudelft.sem.student.controller.StudentControllerRequest;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
import nl.tudelft.sem.student.valueobjects.CompanyRequestModification;
import nl.tudelft.sem.student.valueobjects.ContractModification;
import nl.tudelft.sem.student.valueobjects.Feedback;
import nl.tudelft.sem.student.valueobjects.FilterTag;
import nl.tudelft.sem.student.valueobjects.StudentRequest;
import nl.tudelft.sem.student.valueobjects.StudentRequestChange;
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
public class StudentControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final String netId = "MyNetId";
    private final String company = "amazon94";
    private final String studentId = "mherrebout";
    private final Long modificationId = 1L;

    private final Long contractId = 13L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MockMvc mockMvcContract;
    @Autowired
    private MockMvc mockMvcFeedback;
    @Autowired
    private MockMvc mockMvcRequest;

    @Mock
    private StudentService studentService;
    @Mock
    private StudentServiceContract studentServiceContract;
    @Mock
    private StudentServiceFeedback studentServiceFeedback;
    @Mock
    private StudentServiceRequest studentServiceRequest;

    /**
     * Set the right mockMvc to handle the refactored controllers.
     */
    @BeforeEach
    public void setup() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(new StudentController(studentService,
                studentServiceContract, studentServiceFeedback, studentServiceRequest))
                    .build();
        this.mockMvcContract =
            MockMvcBuilders.standaloneSetup(new StudentControllerContract(
                    studentService,
                studentServiceContract, studentServiceFeedback, studentServiceRequest))
                    .build();
        this.mockMvcFeedback =
                MockMvcBuilders.standaloneSetup(new StudentControllerFeedback(
                        studentService,
                        studentServiceContract, studentServiceFeedback, studentServiceRequest))
                        .build();
        this.mockMvcRequest =
                MockMvcBuilders.standaloneSetup(new StudentControllerRequest(
                        studentService,
                        studentServiceContract, studentServiceFeedback, studentServiceRequest))
                        .build();

    }

    /**
     * Tests the get request that returns all students in the database.
     * Verifies that the request returns status code 200.
     * Verifies that <code>getAll()</code> in StudentService is invoked.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/student/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentService).getAll();
    }

    /**
     * Tests the get request that returns a specific user using their netId.
     * Verifies that the request returns status code 200.
     * Verifies that <code>findStudentByNetId()</code> in StudentService
     * is invoked for the given userId.
     *
     * @throws Exception if the status code is not 200
     */
    @Test
    public void testFindStudentByNetId() throws Exception {
        mockMvc.perform(get("/student/{netId}/", netId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentService).findStudentByNetId(netId);
    }

    @Test
    public void testSaveStudent() throws Exception {
        String studentPayload =
            "{\n    \"netId\": \"revirator\",\n" + "    \"name\": \"Denis Tsvetkov\",\n"
                + "    \"totalHours\": 0,\n" + "    \"available\": false,\n"
                + "    \"averageRating\": 1\n" + "}";
        mockMvc.perform(post("/student/" + netId + "/")
                        .content(studentPayload)
                        .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        Student student = gson.fromJson(studentPayload, Student.class);
        verify(studentService).saveStudent(netId, student);
    }

    @Test
    public void testGetFeedbackForStudent() throws Exception {
        // "test" is just a dummy student ID
        mockMvcFeedback.perform(get("/student/test/feedback/{username}", company))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceFeedback).getFeedbackForCompany(company);
    }

    @Test
    public void testPostFeedbackForStudent() throws Exception {
        String feedbackPayload =
            "{\n    \"starRating\": 1,\n" + "    \"description\": \"Would not recommend.\"\n" + "}";
        // "test" is just a dummy test ID
        mockMvcFeedback.perform(post("/student/test/feedback/{username}", company)
                        .content(feedbackPayload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        Feedback feedback = gson.fromJson(feedbackPayload, Feedback.class);
        verify(studentServiceFeedback).postFeedbackForCompany(company, feedback);
    }

    @Test
    public void testGetFeedbackForCompany() throws Exception {
        // "test" is just a dummy student ID
        mockMvcFeedback.perform(get("/student/test/feedback/{companyId}", company))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceFeedback).getFeedbackForCompany(company);
    }

    @Test
    public void testPostFeedbackForCompany() throws Exception {
        String feedbackPayload =
            "{\n    \"starRating\": 5,\n" + "    \"description\": \"Great company.\"\n" + "}";
        // "test" is just a dummy student ID
        mockMvcFeedback.perform(post("/student/test/feedback/{companyId}", company)
                        .content(feedbackPayload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        Feedback feedback = gson.fromJson(feedbackPayload, Feedback.class);
        verify(studentServiceFeedback).postFeedbackForCompany(company, feedback);
    }

    @Test
    public void testGetAllCompanyRequests() throws Exception {
        mockMvcRequest.perform(get("/student/{studentId}/companyRequests", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).getAllRequests();
    }

    @Test
    public void testGetRequestsByCompanyId() throws Exception {
        mockMvcRequest.perform(get("/student/studentId/request/{companyId}", company))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).getRequestByCompanyId(company);
    }

    @Test
    public void testFindForSalary() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"salary\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvcRequest.perform(post("/student/studentId/request/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(studentServiceRequest).applyFilter(resultFilter);
    }

    @Test
    public void testFindForHoursPerWeek() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"hoursPerWeek\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvcRequest.perform(post("/student/studentId/request/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(studentServiceRequest).applyFilter(resultFilter);
    }

    @Test
    public void testFindForTotalHours() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"totalHours\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvcRequest.perform(post("/student/studentId/request/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(studentServiceRequest).applyFilter(resultFilter);
    }

    @Test
    public void testPostStudentRequest() throws Exception {
        String payload = "{\n    \"hoursPerWeek\": 9,\n    \"totalHours\": 43,\n"
            + "    \"salaryPerHour\": 4,\n    \"expertise\": []\n}";
        mockMvcRequest.perform(post("/student/{studentId}/request", studentId).content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        StudentRequest request = gson.fromJson(payload, StudentRequest.class);
        verify(studentServiceRequest).postStudentRequest(studentId, request);
    }

    @Test
    public void testDeleteStudentRequest() throws Exception {
        mockMvcRequest.perform(delete("/student/{studentId}/request", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).deleteStudentRequest(studentId);
    }

    @Test
    public void testUpdateStudentRequest() throws Exception {
        String payload = "{\n    \"salaryPerHour\": 20\n}";
        mockMvcRequest.perform(put("/student/{studentId}/request", studentId).content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        StudentRequestChange changeData = gson.fromJson(payload, StudentRequestChange.class);
        verify(studentServiceRequest).updateStudentRequest(studentId, changeData);
    }

    @Test
    public void testGetTargetedRequests() throws Exception {
        mockMvcRequest.perform(get("/student/studentId/request/getAll/targeted"))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).getTargetedRequest("studentId");
    }

    @Test
    public void testAcceptByStudent() throws Exception {
        String json = "";
        String studentId = "revirator";
        Long serviceId = 1L;
        mockMvcRequest.perform(post("/student/{studentId}/request/acceptOwn/{serviceId}",
                        studentId, serviceId).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).acceptByStudent(studentId, serviceId);
    }

    @Test
    public void testAcceptCompanyRequest() throws Exception {
        String json = "";
        String studentId = "revirator";
        Long serviceId = 1L;
        mockMvcRequest.perform(
                post("/student/{studentId}/request/accept/{serviceId}", studentId, serviceId)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).acceptCompanyRequest(studentId, serviceId);
    }

    @Test
    public void testProposeModification() throws Exception {
        String modificationPayload = "{\n" + "        \"modificationType\": \"MODIFICATION\",\n"
            + "        \"studentID\": \"2351353\",\n" + "        \"companyID\": \"1351351\",\n"
            + "        \"hoursPerWeek\": 4,\n" + "        \"totalHours\": 16,\n"
            + "        \"pricePerHour\": 12.5,\n" + "        \"acceptedByCompany\": false,\n"
            + "        \"acceptedByStudent\": true,\n" + "        \"finished\": true\n"
            + "}";
        mockMvcContract.perform(post("/student/{studentId}/modification/proposes", studentId)
                .content(modificationPayload)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        ContractModification modification =
            gson.fromJson(modificationPayload, ContractModification.class);
        verify(studentServiceContract).proposeModification(modification);
    }

    @Test
    public void testAcceptModification() throws Exception {
        mockMvcContract.perform(
                post("/student/{studentId}/modification/accepts/{modificationId}", studentId,
                    modificationId)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).acceptModification(studentId, modificationId);
    }

    @Test
    public void testDeclineModification() throws Exception {
        mockMvcContract.perform(
                post("/student/{studentId}/modification/declines/{modificationId}", studentId,
                    modificationId)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).declineModification(studentId, modificationId);
    }

    @Test
    public void testGetProposedModifications() throws Exception {
        mockMvcContract.perform(get("/student/{studentID}/modification/proposed", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).getModificationsToRespond(studentId);
    }

    @Test
    public void testGetAllInvolvedInModifications() throws Exception {
        mockMvcContract.perform(get("/student/{studentID}/modification/all", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).getAllInvolvedInModifications(studentId);
    }

    @Test
    public void testGetAllContracts() throws Exception {
        mockMvcContract.perform(get("/student/{studentId}/contracts", studentId))
                .andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).getAllContracts(studentId);
    }

    @Test
    public void testGetContract() throws Exception {
        mockMvcContract.perform(get("/student/{studentId}/contract/{contractId}",
                        studentId, contractId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceContract).getContract(contractId);
    }

    @Test
    public void testRequestChangesToCompanyRequest() throws Exception {
        final String payload = "{\n"
                + "        \"hoursPerWeek\": 4,\n"
                + "        \"totalHours\": 16,\n"
                + "        \"pricePerHour\": 12.5\n"
                + "}";
        final CompanyRequestModification modification = gson
                .fromJson(payload, CompanyRequestModification.class);
        mockMvcRequest.perform(post("/student/{studentId}/request/change/{serviceId}",
                        studentId, 1L)
                        .content(payload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentServiceRequest).requestChangesToCompanyRequest(studentId, 1L, modification);
    }
}
