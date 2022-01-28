package nl.tudelft.sem.contract.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.entity.ContractModification;
import nl.tudelft.sem.contract.repository.ContractRepository;
import nl.tudelft.sem.contract.repository.ModificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
@Service
@Slf4j  //A logger
public class ModificationService {
    private final ModificationRepository modificationRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public ModificationService(ModificationRepository modificationRepository,
                               ContractRepository contractRepository) {
        this.modificationRepository = modificationRepository;
        this.contractRepository = contractRepository;
    }

    /**
     * Saves the contract modification to JPA repository if the type of modification
     * is correct.
     *
     * @param contractModification The modification of the contract to be saved to the repository.
     * @return The saved modification or null if the condition is not met
     */
    public ContractModification saveModification(ContractModification contractModification) {
        if (contractModification.getModificationType().equals("EXTENSION")
            || contractModification.getModificationType().equals("MODIFICATION")) {
            return saveCorrectLengthModification(contractModification);
        } else if (contractModification.getModificationType().equals("TERMINATION")) {
            return modificationRepository.save(contractModification);
        }
        return null;

    }

    /**
     * Helper method for save to check if the duration is less than
     * or equal to six months ( for extension and modification)
     * and the hoursPerWeeks is less than or equal to 20.
     * The number of weeks is calculated by dividing the total number of hours
     * by hours per week.
     * Then number of months is calculated by dividing the total number of weeks
     * by 4 ( usually a month has about 4 weeks).
     *
     * @param contractModification The modification of the contract to be saved to the repository.
     * @return The saved modification or null if the condition is not met
     */
    private ContractModification saveCorrectLengthModification(
        ContractModification contractModification) {
        double numberOfMonths =
            (contractModification.getTotalHours() / contractModification.getHoursPerWeek()) / 4.00;
        if (numberOfMonths <= 6.00 && contractModification.getHoursPerWeek() <= 20) {
            return modificationRepository.save(contractModification);
        }
        return null;
    }

    /**
     * Updates the contract modification to JPA repository.
     *
     * @param contractModification The modification of the contract to be updated to the repository.
     * @return The updated modification
     */
    public ContractModification updateModification(ContractModification contractModification) {
        return modificationRepository.saveAndFlush(contractModification);
    }

    /**
     * Deletes the contract modification to JPA repository.
     *
     * @param contractModification The modification of the contract to be deleted from the
     *                             repository.
     */
    public void deleteModification(ContractModification contractModification) {
        modificationRepository.delete(contractModification);
    }

    /**
     * To get all the proposals of contract modification for which the student
     * needs to give a response to.
     *
     * @param studentId the student to search for proposals
     * @return the list of the proposals of contract modification for the student
     */
    public List<ContractModification> getProposalsForStudent(String studentId) {
        return modificationRepository.findAllByStudentIdAndAcceptedByCompanyAndFinished(studentId,
            true, false);
    }

    /**
     * To get all the proposals of contract modification which the company
     * needs to give a response to.
     *
     * @param companyId the company to search for proposals
     * @return the list of the proposals of contract modification for the company
     */
    public List<ContractModification> getProposalsForCompany(String companyId) {
        return modificationRepository.findAllByCompanyIdAndAcceptedByStudentAndFinished(companyId,
            true, false);
    }

    /**
     * To get all the proposals of contract modifications in which the student has been involved.
     * Proposed by the student or proposed to the student modifications.
     *
     * @param studentId the student to search for all the contract modifications
     * @return the list of the proposals of contract modification for the student
     */
    public List<ContractModification> getStudentInvolvedModifications(String studentId) {
        return modificationRepository.findAllByStudentId(studentId);
    }

    /**
     * To get all the proposals of contract modifications in which the company has been involved.
     * Proposed by the company or proposed to the company modifications.
     *
     * @param companyId the company to search for all the proposals
     * @return the list of the proposals of contract modification for the student
     */
    public List<ContractModification> getCompanyInvolvedModifications(String companyId) {
        return modificationRepository.findAllByCompanyId(companyId);
    }

    /**
     * To get the proposal specific for a contract Id.
     *
     * @param modificationId the id to search for proposal
     * @return the contract modification for that id
     */
    public ContractModification getContractModification(Long modificationId) {
        return modificationRepository.getOne(modificationId);
    }

    /**
     * To modify the contract specified regards to the contractModification.
     *
     * @param modificationId the modification id
     * @param companyAccept  if the acceptance is made by company
     * @return the modified contract
     */
    public Contract acceptModification(Long modificationId, boolean companyAccept) {
        ContractModification contractModification = modificationRepository.getOne(modificationId);
        /*
        Check whether the acceptance of the proposal is made by company or student
         */
        if (companyAccept == true) {
            contractModification.setAcceptedByCompany(true);
        } else {
            contractModification.setAcceptedByStudent(true);
        }

        return acceptGoodTermsModification(contractModification);

    }

    /**
     * Helper method to generate the new contract.
     * Checks whether the proposal is accepted by both parties and if it is not rejected.
     *
     * @param contractModification the modification
     * @return resulted contract or null if the condition is not met
     */
    private Contract acceptGoodTermsModification(ContractModification contractModification) {
        if ((contractModification.isAcceptedByCompany()
            && contractModification.isAcceptedByStudent()) && !contractModification.isFinished()) {
            return acceptExistingContract(contractModification);
        }
        terminateModification(contractModification);
        return null;
    }

    /**
     * Helper method to generate the new contract.
     * Checks whether the contract linked with the proposal exists.
     *
     * @param contractModification the modification
     * @return resulted contract or null if the condition is not met
     */
    private Contract acceptExistingContract(ContractModification contractModification) {
        if (contractRepository.existsById(contractModification.getContractId())) {
            return acceptGoodTypeOfModification(contractModification);

        }
        terminateModification(contractModification);
        return null;
    }

    /**
     * Helper method to generate the new contract.
     * Checks whether the proposal is one of the accepted types.
     *
     * @param contractModification the modification
     * @return resulted contract or null if the condition is not met
     */
    private Contract acceptGoodTypeOfModification(ContractModification contractModification) {
        if (contractModification.getModificationType().equals("TERMINATION")) {
            contractModification = terminateModification(contractModification);
            return terminateContract(
                contractRepository.getOne(contractModification.getContractId()));
        } else {
            if (contractModification.getModificationType().equals("EXTENSION")) {
                contractModification = terminateModification(contractModification);
                return extendContract(
                    contractRepository.getOne(contractModification.getContractId()),
                    contractModification.getTotalHours());
            } else {
                if (contractModification.getModificationType().equals("MODIFICATION")) {
                    contractModification = terminateModification(contractModification);
                    return modifyContract(
                        contractRepository.getOne(contractModification.getContractId()),
                        contractModification);
                } else {
                    terminateModification(contractModification);
                    return null;
                }
            }

        }
    }

    private ContractModification terminateModification(ContractModification contractModification) {
        contractModification.setFinished(true);
        return modificationRepository.saveAndFlush(contractModification);
    }

    /**
     * Used to save into the Repository the changes from termination.
     *
     * @param contract the terminated contract
     * @return the terminated contract
     */
    public Contract terminateContract(Contract contract) {
        contract.setTerminated(true);
        return contractRepository.saveAndFlush(contract);
    }

    /**
     * Used to save into the Repository the changes from extension.
     *
     * @param contract   the terminated contract
     * @param totalHours the new total hours
     * @return the extended contract
     */
    public Contract extendContract(Contract contract, int totalHours) {
        contract.setTotalHours(totalHours);
        return contractRepository.saveAndFlush(contract);
    }

    /**
     * Used to save into the Repository the changes from modification.
     *
     * @param contract             the terminated contract
     * @param contractModification the modification applied
     * @return the modified contract
     */
    public Contract modifyContract(Contract contract, ContractModification contractModification) {
        int newHoursPerWeek = contractModification.getHoursPerWeek();
        double newPricePerHour = contractModification.getPricePerHour();
        int newTotalHours = contractModification.getTotalHours();

        contract.setTotalHours(newTotalHours);
        contract.setPricePerHour(newPricePerHour);
        contract.setHoursPerWeek(newHoursPerWeek);

        return contractRepository.saveAndFlush(contract);

    }

    /**
     * To decline the contract modification.
     *
     * @param modificationId the modification id
     * @param companyDecline if the declination is made by company
     * @return the modified contract
     */
    public ContractModification declineModification(Long modificationId, Boolean companyDecline) {
        ContractModification contractModification = modificationRepository.getOne(modificationId);

        /*
        Check whether the declination of the proposal is made by company or student
         */
        if (companyDecline == true) {
            contractModification.setAcceptedByCompany(false);
        } else {
            contractModification.setAcceptedByStudent(false);
        }
        contractModification.setFinished(true);
        return modificationRepository.saveAndFlush(contractModification);
    }

}
