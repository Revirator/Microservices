package nl.tudelft.sem.contract.tests.controller;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import nl.tudelft.sem.contract.controller.ContractController;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class ContractControllerTest {

    private final Gson gson = new GsonBuilder().create();
    private final long contractId = 2L;
    private final String apple = "Apple";
    private final String ahmet = "Ahmet";
    private final Contract contract = new Contract(2L, 4, 16, 12.5, ahmet, apple, false);
    private final Contract contract2 = new Contract(3L, 5, 20, 13.5, "Denis", "Google", false);
    private final Contract contract3 = new Contract(4L, 4, 16, 12.5, ahmet, "Meta", false);
    private final Contract contract4 = new Contract(5L, 4, 16, 12.5, "George", apple, false);

    private final String payload =
        "{\n" + "        \"contractId\": 2,\n" + "        \"hoursPerWeek\": 4,\n"
            + "        \"totalHours\": 16,\n" + "        \"pricePerHour\": 12.5,\n"
            + "        \"studentId\": \"Ahmet\",\n" + "        \"companyId\": \"Apple\",\n"
            + "        \"terminated\": false\n" + "}";
    @Autowired
    private MockMvc mockMvc;
    @Mock
    private ContractService contractService;

    @BeforeEach
    public void setup() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(new ContractController(contractService)).build();
    }

    /**
     * Tests saving a contract to JPA repository via an API call.
     *
     * @throws Exception if status code is not 200.
     */
    @Test
    public void testSaveContract() throws Exception {
        when(contractService.saveContract(contract)).thenReturn(contract);
        MvcResult result = mockMvc
            .perform(post("/contracts/").content(payload).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();
        Contract contract2 = gson.fromJson(payload, Contract.class);
        verify(contractService).saveContract(contract2);
        assertTrue(result.getResponse().getContentAsString().contains(ahmet));
    }

    /**
     * Tests getting a contract via contractID from JPA repository via an API call.
     *
     * @throws Exception if status code is not 200.
     */
    @Test
    public void testGetContract() throws Exception {
        when(contractService.getContractById(contractId)).thenReturn(
            java.util.Optional.of(contract));
        MvcResult result = mockMvc.perform(get("/contracts/{contractId}", contractId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(apple));
        verify(contractService).getContractById(contractId);
    }

    /**
     * Tests getting a contract via contractID from JPA repository via an API call.
     *
     * @throws Exception if status code is not 200.
     */
    @Test
    public void testFindAllContracts() throws Exception {
        when(contractService.findAllContracts()).thenReturn(List.of(contract, contract2));
        MvcResult result = mockMvc.perform(get("/contracts/")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(ahmet));
        assertTrue(result.getResponse().getContentAsString().contains("Denis"));
        verify(contractService).findAllContracts();
    }

    @Test
    public void testGetAllContractsByStudentId() throws Exception {
        String id = "Ahmet";
        when(contractService.findContractsByStudentId(id)).thenReturn(List.of(contract, contract3));
        MvcResult result = mockMvc.perform(get("/contracts/student/{id}", id)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(ahmet));
        assertTrue(result.getResponse().getContentAsString().contains("Meta"));
        assertTrue(result.getResponse().getContentAsString().contains(apple));
        verify(contractService).findContractsByStudentId(id);
    }

    @Test
    public void testGetAllContractsByCompanyId() throws Exception {
        String id = "Apple";
        when(contractService.findContractsByCompanyId(id)).thenReturn(List.of(contract, contract4));
        MvcResult result = mockMvc.perform(get("/contracts/company/{id}", id)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains(apple));
        assertTrue(result.getResponse().getContentAsString().contains("George"));
        assertTrue(result.getResponse().getContentAsString().contains(ahmet));
        verify(contractService).findContractsByCompanyId(id);
    }
}