package nl.tudelft.sem.contract.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.contract.entity.Contract;
import nl.tudelft.sem.contract.entity.ContractModification;
import nl.tudelft.sem.contract.service.ModificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/modification")
@Slf4j //The logger
public class ModificationController {

    private final ModificationService modificationService;

    @Autowired
    public ModificationController(ModificationService modificationService) {
        this.modificationService = modificationService;
    }

    /**
     * POST request to save a modification to the JPA repository.
     *
     * @param contractModification The modification to be saved.
     * @return The modification if saved successfully to JPA repository.
     */
    @PostMapping("/")
    public ContractModification saveModification(
        @RequestBody ContractModification contractModification) {
        return modificationService.saveModification(contractModification);
    }

    /**
     * GET request to get all modifications proposed to a student by studentID via a path
     * variable.
     *
     * @param studentId The ID of the student.
     * @return A list of modifications
     */
    @GetMapping("/student/{id}")
    public List<ContractModification> getProposedModificationsForStudent(
        @PathVariable("id") String studentId) {
        return modificationService.getProposalsForStudent(studentId);
    }

    /**
     * GET request to get all modification proposals in which the student is involved.
     *
     * @param studentId The ID of the student.
     * @return A list of modifications
     */
    @GetMapping("/all/student/{id}")
    public List<ContractModification> getStudentInvolvedModifications(
        @PathVariable("id") String studentId) {
        return modificationService.getStudentInvolvedModifications(studentId);
    }

    /**
     * GET request to get all modifications proposed to a company by companyId via a path
     * variable.
     *
     * @param companyId The ID of the company.
     * @return A list of modifications
     */
    @GetMapping("/company/{id}")
    public List<ContractModification> getProposedModificationsForCompany(
        @PathVariable("id") String companyId) {
        return modificationService.getProposalsForCompany(companyId);
    }

    /**
     * GET request to get all modification proposals in which the company is involved.
     *
     * @param companyId The ID of the company.
     * @return A list of modifications
     */
    @GetMapping("/all/company/{id}")
    public List<ContractModification> getCompanyInvolvedModifications(
        @PathVariable("id") String companyId) {
        return modificationService.getCompanyInvolvedModifications(companyId);
    }

    /**
     * POST request to have the modification accepted by the company.
     *
     * @param companyId      the id of the company which is going to accept
     * @param modificationId the id of the modification that is going to be accepted
     */
    @PostMapping("/company/{companyId}/accepts/{acceptedModificationId}")
    @ResponseBody
    public Contract acceptByCompany(@PathVariable("companyId") String companyId,
                                    @PathVariable("acceptedModificationId") Long modificationId) {
        return modificationService.acceptModification(modificationId, true);

    }

    /**
     * POST request to have the modification declined by the company.
     *
     * @param companyId      the id of the company which is going to decline
     * @param modificationId the id of the modification that is going to be declined
     * @return the declined modification
     */
    @PostMapping("/company/{companyId}/declines/{declinedModificationId}")
    @ResponseBody
    public ContractModification declineByCompany(@PathVariable("companyId") String companyId,
                                                 @PathVariable("declinedModificationId")
                                                     Long modificationId) {
        return modificationService.declineModification(modificationId, true);

    }

    /**
     * POST request to have the modification accepted by the student.
     *
     * @param studentId      the id of the student which is going to accept
     * @param modificationId the id of the modification that is going to be accepted
     */
    @PostMapping("/student/{studentId}/accepts/{acceptedModificationId}")
    @ResponseBody
    public Contract acceptByStudent(@PathVariable("studentId") String studentId,
                                    @PathVariable("acceptedModificationId") Long modificationId) {
        return modificationService.acceptModification(modificationId, false);
    }

    /**
     * POST request to have the modification declined by the student.
     *
     * @param studentId      the id of the student which is going to decline
     * @param modificationId the id of the modification that is going to be declined
     * @return the declined modification
     */
    @PostMapping("/student/{studentId}/declines/{declinedModificationId}")
    @ResponseBody
    public ContractModification declineByStudent(@PathVariable("studentId") String studentId,
                                                 @PathVariable("declinedModificationId")
                                                     Long modificationId) {
        return modificationService.declineModification(modificationId, false);

    }

}
