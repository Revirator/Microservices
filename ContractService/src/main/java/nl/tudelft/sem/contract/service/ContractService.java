package nl.tudelft.sem.contract.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
@Service
@Slf4j  //A logger
public class ContractService {

    private final ContractRepository contractRepository;

    @Autowired
    public ContractService(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    /**
     * Saves the contract to JPA repository if the duration is less than
     * or equal to six months and the hoursPerWeek are less than or equal to 20 hours.
     * The number of weeks is calculated by dividing the total number of hours
     * by hours per week.
     * Then number of months is calculated by dividing the total number of weeks
     * by 4 ( usually a month has about 4 weeks).
     *
     * @param contract The contract to be saved to the repository.
     * @return The saved contract
     */
    public Contract saveContract(Contract contract) {
        log.info("Inside saveContract method of ContractService");
        double numberOfMonths = (contract.getTotalHours() / contract.getHoursPerWeek()) / 4.00;
        if (numberOfMonths <= 6.00 && contract.getHoursPerWeek() <= 20) {
            return contractRepository.save(contract);
        }
        return null;

    }

    /**
     * Gets contract from JPA repository by contractID.
     *
     * @param contractId The ID of the contract
     * @return The contract with the contractID provided.
     */
    public Optional<Contract> getContractById(Long contractId) {
        log.info("Inside getContractByID method of ContractService");
        return contractRepository.findById(contractId);
    }

    /**
     * Finds all contracts in the JPA repository.
     *
     * @return All the existing contracts in the JPA repository.
     */
    public List<Contract> findAllContracts() {
        log.info("Inside getAllContracts method of ContractService");
        return contractRepository.findAll();
    }

    /**
     * Finds all contracts that have the same student ID.
     *
     * @param studentId The ID of the student.
     * @return A list of contracts with the same student ID provided.
     */
    public List<Contract> findContractsByStudentId(String studentId) {
        log.info("Inside findContractsByStudentId method of ContractService");
        return contractRepository.findAllByStudentId(studentId);
    }

    /**
     * Finds all contracts that have the same company ID.
     *
     * @param companyId The ID of the company.
     * @return A list of contracts with the same company ID provided.
     */
    public List<Contract> findContractsByCompanyId(String companyId) {
        log.info("Inside findContractsByCompanyId method of ContractService");
        return contractRepository.findAllByCompanyId(companyId);
    }

}
