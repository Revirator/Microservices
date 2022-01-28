package nl.tudelft.sem.contract.tests.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.contract.controller.ModificationController;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.entity.ContractModification;
import nl.tudelft.sem.contract.service.ModificationService;
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
public class ModifControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final long modificationId = 1L;
    private final String companyId = "Apple";
    private final String studentId = "Bogdan";
    private final ContractModification contractModif =
        new ContractModification(1L, "MODIFICATION", 1L, "student1", "company1", 15, 60, 5, false,
            false, false);
    private final String payload = "{\n" + "        \"modificationId\": 1,\n"
        + "        \"modificationType\": \"MODIFICATION\",\n"
        + "        \"studentID\": \"2351353\",\n" + "        \"companyID\": \"1351351\",\n"
        + "        \"hoursPerWeek\": 4,\n" + "        \"totalHours\": 16,\n"
        + "        \"pricePerHour\": 12.5,\n" + "        \"acceptedByCompany\": true,\n"
        + "        \"acceptedByStudent\": false,\n" + "        \"finished\": false\n"
        + "}";
    private final Contract contract = new Contract(4L, 15, 60, 15, "student4", "company4", false);

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ModificationService modificationService;

    /**
     * The setup of the controller test.
     */
    @BeforeEach
    public void setup() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(new ModificationController(modificationService))
                .build();
    }

    @Test
    public void testSaveModification() throws Exception {
        mockMvc.perform(
                post("/modification/").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        ContractModification contractModification =
            gson.fromJson(payload, ContractModification.class);
        verify(modificationService).saveModification(contractModification);
    }

    @Test
    public void testGetProposalsForStudent() throws Exception {

        mockMvc.perform(get("/modification/student/{id}", studentId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(modificationService).getProposalsForStudent(studentId);
    }

    @Test
    public void testGetProposalsForCompany() throws Exception {

        mockMvc.perform(get("/modification/company/{id}", companyId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(modificationService).getProposalsForCompany(companyId);
    }

    @Test
    public void testAcceptsForCompany() throws Exception {

        given(modificationService.acceptModification(modificationId, true)).willReturn(contract);
        mockMvc.perform(
            post("/modification/company/{companyId}/accepts/{modificationId}", companyId,
                modificationId)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

        verify(modificationService).acceptModification(modificationId, true);

    }

    @Test
    public void testDeclinesForCompany() throws Exception {

        given(modificationService.declineModification(1L, true)).willReturn(contractModif);

        mockMvc.perform(
            post("/modification/company/{companyId}/declines/{modificationId}", companyId,
                modificationId)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

        verify(modificationService).declineModification(1L, true);

    }

    @Test
    public void testAcceptsForStudent() throws Exception {
        given(modificationService.acceptModification(modificationId, false)).willReturn(contract);

        mockMvc.perform(
            post("/modification/student/{studentId}/accepts/{modificationId}", studentId,
                modificationId)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(modificationService).acceptModification(modificationId, false);

    }

    @Test
    public void testDeclineForStudent() throws Exception {

        given(modificationService.declineModification(1L, false)).willReturn(contractModif);
        mockMvc.perform(
            post("/modification/student/{studentId}/declines/{modificationId}", studentId,
                modificationId)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());

        verify(modificationService).declineModification(1L, false);

    }

    @Test
    public void testGetStudentInvolvedModifications() throws Exception {

        mockMvc.perform(get("/modification/all/student/{id}", studentId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(modificationService).getStudentInvolvedModifications(studentId);
    }

    @Test
    public void testGetCompanyInvolvedModifications() throws Exception {

        mockMvc.perform(get("/modification/all/company/{id}", companyId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print());
        verify(modificationService).getCompanyInvolvedModifications(companyId);
    }

}
