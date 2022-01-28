package nl.tudelft.sem.contract.controller;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contracts")
@Slf4j //The logger
public class ContractController {

    private final ContractService contractService;

    @Autowired
    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }

    /**
     * POST request to save a contract to the JPA repository.
     *
     * @param contract The contract to be saved.
     * @return The contract if saved successfully to JPA repository.
     */
    @PostMapping("/")
    public Contract saveContract(@RequestBody Contract contract) {
        log.info("Inside saveContract method of ContractController");
        return contractService.saveContract(contract);
    }

    /**
     * GET request to get all existing contracts.
     *
     * @return A list of contracts if there exists contracts.
     */
    @GetMapping("/")
    public List<Contract> getAllContracts() {
        log.info("Inside getContractsByStudentID method of ContractController");
        return contractService.findAllContracts();
    }

    /**
     * GET request to get a contract by contractID via a path variable.
     *
     * @param contractId The ID of the contract.
     * @return The contract if there exists a contract with the provided ID.
     */
    @GetMapping("/{id}")
    public Optional<Contract> getContractById(@PathVariable("id") Long contractId) {
        log.info("Inside getContractByID method of ContractController");
        return contractService.getContractById(contractId);
    }

    /**
     * GET request to get all contracts of a student by studentID via a path variable.
     *
     * @param studentId The ID of the student.
     * @return A list of contracts if there exists contracts with the provided student ID.
     */
    @GetMapping("/student/{id}")
    public List<Contract> getContractsByStudentId(@PathVariable("id") String studentId) {
        log.info("Inside getContractsByStudentId method of ContractController");
        return contractService.findContractsByStudentId(studentId);
    }

    /**
     * GET request to get all contracts of company by companyID via a path variable.
     *
     * @param companyId The ID of the company.
     * @return A list of contracts if there exists contracts with the provided student ID.
     */
    @GetMapping("/company/{id}")
    public List<Contract> getContractsByCompanyId(@PathVariable("id") String companyId) {
        log.info("Inside getContractsByCompanyId method of ContractController");
        return contractService.findContractsByCompanyId(companyId);
    }

}
