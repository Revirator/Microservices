package nl.tudelft.sem.student.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.service.StudentService;
import nl.tudelft.sem.student.service.StudentServiceContract;
import nl.tudelft.sem.student.service.StudentServiceFeedback;
import nl.tudelft.sem.student.service.StudentServiceRequest;
import nl.tudelft.sem.student.valueobjects.Contract;
import nl.tudelft.sem.student.valueobjects.ContractModification;
import nl.tudelft.sem.student.valueobjects.ModificationResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Slf4j
public class StudentControllerContract extends StudentController {

    public StudentControllerContract(StudentService studentService,
                                     StudentServiceContract studentServiceContract,
                                     StudentServiceFeedback studentServiceFeedback,
                                     StudentServiceRequest studentServiceRequest) {
        super(studentService, studentServiceContract, studentServiceFeedback,
                studentServiceRequest);
    }

    /**
     * Sends request to the Contract microservice to accept a contractModification as a student.
     *
     * @param studentId      The ID of the student
     * @param modificationId The ID of the modification.
     * @return the modified contract
     */
    @PostMapping("/{studentId}/modification/accepts/{modificationId}")
    @ResponseBody
    public Contract acceptModification(@PathVariable String studentId,
                                       @PathVariable Long modificationId) {
        return studentServiceContract.acceptModification(studentId, modificationId);

    }

    /**
     * Sends request to the Contract microservice to decline a contractModification as a student.
     *
     * @param studentId      The ID of the student
     * @param modificationId The ID of the modification.
     * @return the declined modification
     */
    @PostMapping("/{studentId}/modification/declines/{modificationId}")
    @ResponseBody
    public ContractModification declineModification(@PathVariable String studentId,
                                                    @PathVariable Long modificationId) {
        return studentServiceContract.declineModification(studentId, modificationId);

    }

    /**
     * Sends request to the Contract microservice to propose a contractModification as a student.
     *
     * @param studentId    The ID of the company
     * @param modification the modification to be proposed
     * @return the modification
     */
    @PostMapping("/{studentId}/modification/proposes")
    @ResponseBody
    public ContractModification proposeModification(
            @PathVariable String studentId,
            @RequestBody ContractModification modification) {
        return studentServiceContract.proposeModification(modification);

    }

    /**
     * Sends a request to the Contract Microservice to get the proposed modifications from
     * companies.
     *
     * @param studentId the ID of the student
     * @return a List of all the modifications that were proposed by the companies to that student.
     */
    @GetMapping("/{studentId}/modification/proposed")
    @ResponseBody
    public ModificationResponse getProposedModificationsByStudentId(
            @PathVariable String studentId) {
        return studentServiceContract.getModificationsToRespond(studentId);
    }

    /**
     * Sends a request to the Contract Microservice to get the all the proposals of modifications
     * in which that student has been involved.
     *
     * @param studentId the ID of the student
     * @return a List of all the modifications
     */
    @GetMapping("/{studentId}/modification/all")
    @ResponseBody
    public ModificationResponse getAllInvolvedInModifications(@PathVariable String studentId) {
        return studentServiceContract.getAllInvolvedInModifications(studentId);
    }

    /**
     * Gets all a student's contracts.
     *
     * @param studentId - the id of the student.
     * @return a list of all the contracts of the student.
     */
    @GetMapping("/{studentId}/contracts")
    @ResponseBody
    public List<Contract> getAllContracts(@PathVariable String studentId) {
        log.info("Inside getAllContracts of StudentController class.");
        return studentServiceContract.getAllContracts(studentId);
    }

    /**
     * Gets a particular contract.
     *
     * @param studentId  - the id of the student.
     * @param contractId - the id of the contract.
     * @return the contract that was requested.
     */
    @GetMapping("/{studentId}/contract/{contractId}")
    @ResponseBody
    public Contract getContract(@PathVariable String studentId, @PathVariable Long contractId) {
        return studentServiceContract.getContract(contractId);
    }
}
