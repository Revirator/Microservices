package nl.tudelft.sem.request.test.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import nl.tudelft.sem.request.controller.RequestController;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import nl.tudelft.sem.request.service.CompanyRequestService;
import nl.tudelft.sem.request.service.StudentRequestService;
import nl.tudelft.sem.request.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.request.valueobjects.FilterTag;
import nl.tudelft.sem.request.valueobjects.StudentRequestChange;
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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ControllerTest {

    private final Gson gson = new GsonBuilder().create();

    private final String companyId = "Company";
    private final String studentId = "Student";
    private final Long serviceId = 1L;
    private final CompanyRequest companyRequest =
        new CompanyRequest(1L, 10, 20, 14, "companyId", List.of("requirements"));

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private CompanyRequestService companyRequestService;
    @Mock
    private StudentRequestService studentRequestService;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(
            new RequestController(companyRequestService, studentRequestService)).build();
    }

    @Test
    public void testGetAll() throws Exception {
        mockMvc.perform(get("/request/student/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).getAllStudentRequests();
    }

    @Test
    public void testGetCompaniesAll() throws Exception {
        mockMvc.perform(get("/request/company/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).getAllCompanyRequests();
    }

    @Test
    public void testFindForCompany() throws Exception {
        mockMvc.perform(get("/request/company/{companyId}/", companyId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).findAllRequestByCompanyId(companyId);
    }

    @Test
    public void testCompanyRequestFindForSalary() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"salary\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService).findAllCompanyRequestsBySalaryPerHour(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testCompanyRequestFindForHoursPerWeek() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"hoursPerWeek\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService).findAllCompanyRequestsByHoursPerWeek(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testCompanyRequestFindForTotalHours() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"totalHours\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService).findAllCompanyRequestsByTotalHours(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testLargerStartOfTheInterval() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"totalHours\",\n"
            + "    \"startOfTheInterval\": 32,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService, times(0)).findAllCompanyRequestsByTotalHours(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testWrongTypeOfFiltering() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"totalHourssssssssss\",\n"
            + "    \"startOfTheInterval\": 12,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService, times(0)).findAllCompanyRequestsByTotalHours(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testNegativeStartOfInterval() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"totalHours\",\n"
            + "    \"startOfTheInterval\": -12,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/company/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(companyRequestService, times(0)).findAllCompanyRequestsByTotalHours(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }

    @Test
    public void testFindForStudent() throws Exception {
        mockMvc.perform(get("/request/student/{studentId}", studentId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).findRequestByStudent(studentId);
    }

    @Test
    public void testDeleteStudentRequest() throws Exception {
        mockMvc.perform(delete("/request/student/{studentId}", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).deleteStudentRequest(studentId);
    }

    @Test
    public void testUpdateStudentRequest() throws Exception {
        String payload = "{\n    \"salaryPerHour\": 20\n}";
        mockMvc.perform(put("/request/student/{studentId}", studentId).content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        StudentRequestChange changeData = gson.fromJson(payload, StudentRequestChange.class);
        verify(studentRequestService).updateStudentRequest(studentId, changeData);
    }

    @Test
    public void testDeleteAllCompanyRequests() throws Exception {
        mockMvc.perform(delete("/request/company/{companyId}", companyId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).deleteAllCompanyRequests(companyId);
    }

    @Test
    public void testDeleteCompanyRequest() throws Exception {
        mockMvc.perform(delete("/request/company/service/{serviceId}", serviceId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).deleteCompanyRequest(serviceId);
    }

    @Test
    public void testUpdateCompanyRequest() throws Exception {
        String payload = "{\n    \"salaryPerHour\": 20\n}";
        mockMvc.perform(put("/request/company/service/{serviceId}", serviceId).content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        CompanyRequestChange changeData = gson.fromJson(payload, CompanyRequestChange.class);
        verify(companyRequestService).updateCompanyRequest(serviceId, changeData);
    }

    @Test
    public void testFindStudentRequestByServiceId() throws Exception {
        mockMvc.perform(get("/request/student/serviceId/{serviceId}", serviceId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).findStudentRequestByServiceId(serviceId);
    }

    @Test
    public void testFindCompanyRequestByServiceId() throws Exception {
        mockMvc.perform(get("/request/company/service/{serviceId}", serviceId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).findCompanyRequestByServiceId(serviceId);
    }

    @Test
    public void testSaveStudentRequest() throws Exception {
        String json = "{\n" + "    \"serviceId\": 24,\n" + "    \"hoursPerWeek\": 16,\n"
            + "    \"totalHours\": 48,\n" + "    \"salaryPerHour\": 14,\n"
            + "    \"studentId\": \"revirator\",\n" + "    \"expertise\": [\"c++\", \"java\"]\n"
            + "}";
        mockMvc.perform(
                post("/request/student/").content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        StudentRequest sr = gson.fromJson(json, StudentRequest.class);
        verify(studentRequestService).saveStudentRequest(sr);
    }

    @Test
    public void testSaveCompanyRequest() throws Exception {
        String json = "{\n" + "    \"serviceId\": 23,\n" + "    \"hoursPerWeek\": 16,\n"
            + "    \"totalHours\": 48,\n" + "    \"salaryPerHour\": 16,\n"
            + "    \"companyId\": \"Microsoft\",\n"
            + "    \"requirements\": [\"Be amazing at coding\", \"java\", \"visual studio\"]\n"
            + "}";
        mockMvc.perform(
                post("/request/company/").content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        CompanyRequest cr = gson.fromJson(json, CompanyRequest.class);
        verify(companyRequestService).saveCompanyRequest(cr);
    }

    @Test
    public void testAcceptCompanyRequest() throws Exception {
        String json = "";
        mockMvc.perform(
                post("/request/company/{studentId}/{serviceId}", studentId, serviceId).content(json)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).acceptCompanyRequest(studentId, serviceId);
    }

    @Test
    public void testRejectStudentAsCompany() throws Exception {
        //Will be done later
    }

    @Test
    public void testAcceptStudentRequest() throws Exception {
        String json = "";
        mockMvc.perform(
                post("/request/student/accept/{companyId}/{serviceId}", companyId, serviceId)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).acceptStudentRequest(companyId, serviceId);
    }

    @Test
    public void testRejectCompanyAsStudent() throws Exception {
        //Will be done later
    }

    @Test
    public void testAcceptOwnRequestStudent() throws Exception {
        String json = "";
        mockMvc.perform(
                post("/request/company/acceptOwn/{companyId}/{serviceId}/{studentId}", companyId,
                    serviceId, studentId).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).acceptOwnRequestCompany(companyId, studentId, serviceId);
    }

    @Test
    public void testAcceptOwnRequestCompany() throws Exception {
        String json = "";
        mockMvc.perform(post("/request/student/acceptOwn/{studentId}/{serviceId}", studentId,
                serviceId).content(json).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).acceptOwnRequestStudent(studentId, serviceId);
    }

    @Test
    public void testGetTargetedRequests() throws Exception {
        mockMvc.perform(get("/request/student/targeted/{studentid}", studentId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).findTargetedRequestsForStudent(studentId);
    }

    @Test
    public void testSearchExpertiseStudents() throws Exception {
        String expertise = "C++";
        mockMvc.perform(get("/request/company/search/{expertise}", expertise))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(studentRequestService).searchExpertiseStudents(expertise);
    }

    @Test
    public void testPostTargetedCompanyRequest() throws Exception {
        String companyRequestJsonObject = gson.toJson(companyRequest);
        String targetedStudentId = "target";
        mockMvc.perform(post("/request/company/targeted/{studentId}", targetedStudentId).content(
                companyRequestJsonObject).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(companyRequestService).saveTargetedCompanyRequest(companyRequest, targetedStudentId);
    }

    @Test
    public void testFilterStudentRequests() throws Exception {
        String payload = "{" + "\n" + "    \"typeOfFiltering\": \"hoursPerWeek\",\n"
            + "    \"startOfTheInterval\": 14,\n" + "    \"endOfTheInterval\": 20\n" + "}";
        mockMvc.perform(post("/request/student/filter/").content(payload)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        FilterTag resultFilter = gson.fromJson(payload, FilterTag.class);
        verify(studentRequestService).findAllStudentRequestsByHours(
            resultFilter.getStartOfTheInterval(), resultFilter.getEndOfTheInterval());
    }
}
