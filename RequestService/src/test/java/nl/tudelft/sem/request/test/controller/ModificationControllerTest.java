package nl.tudelft.sem.request.test.controller;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.tudelft.sem.request.controller.ModificationController;
import nl.tudelft.sem.request.entity.CompanyRequestModification;
import nl.tudelft.sem.request.service.ModificationService;
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
public class ModificationControllerTest {

    private final Gson gson = new GsonBuilder().create();
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ModificationService modificationService;
    private final String payload = "{\n"
            + "        \"studentId\": \"revirator\",\n"
            + "        \"serviceId\": 1,\n"
            + "        \"hoursPerWeek\": 4,\n"
            + "        \"totalHours\": 16,\n"
            + "        \"pricePerHour\": 12.5,\n"
            + "        \"acceptedByCompany\": false\n"
            + "}";
    private final CompanyRequestModification modification = gson
            .fromJson(payload, CompanyRequestModification.class);
    private final String companyId = "amazon94";

    /**
     * Set up the mockMvc.
     */
    @BeforeEach
    public void setup() {
        this.mockMvc =
                MockMvcBuilders.standaloneSetup(new ModificationController(modificationService))
                        .build();
    }

    @Test
    public void testSaveModification() throws Exception {
        mockMvc.perform(post("/modification/{serviceId}", 1L)
                .content(payload)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(modificationService).saveModification(1L, modification);
    }

    @Test
    public void testGetAllModificationsForCompany() throws Exception {
        mockMvc.perform(get("/modification/company/{companyId}/all", companyId))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(modificationService).getAllModificationsForCompany(companyId);
    }

    @Test
    public void testAcceptChanges() throws Exception {
        mockMvc.perform(post("/modification/company/"
                        + "{companyId}/accepts/{modificationId}", companyId, 2L)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(modificationService).acceptChanges(companyId, 2L);
    }

    @Test
    public void testRejectChanges() throws Exception {
        mockMvc.perform(post("/modification/company/"
                        + "{companyId}/rejects/{modificationId}", companyId, 3L)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(modificationService).rejectChanges(companyId, 3L);
    }
}
