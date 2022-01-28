package nl.tudelft.sem.student.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.student.entity.Student;
import nl.tudelft.sem.student.repository.StudentRepository;
import nl.tudelft.sem.student.valueobjects.Contract;
import nl.tudelft.sem.student.valueobjects.ContractModification;
import nl.tudelft.sem.student.valueobjects.ModificationResponse;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class StudentServiceContract extends StudentService {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Gson gson = new GsonBuilder().create();

    public StudentServiceContract(RestTemplate restTemplate, StudentRepository studentRepository) {
        super(restTemplate, studentRepository);
    }

    /**
     * Gets all a student's contracts.
     *
     * @param studentId - the id of the student.
     * @return a list of all the contracts of the student.
     */
    public List<Contract> getAllContracts(String studentId) {
        log.info("Inside getAllContracts of StudentService");
        List<LinkedHashMap> response = restTemplate.getForObject(
            "http://CONTRACT-SERVICE/contracts/student/" + studentId, List.class);
        List<Contract> contracts = responseToList(response, Contract.class);
        return contracts;
    }

    /**
     * Sends request to the Contract microservice to propose a contractModification as a student.
     *
     * @param contractModification the modification to be proposed
     * @return the modification
     */
    public ContractModification proposeModification(ContractModification contractModification) {
        //        log.info("Proposed modification for a contract");
        contractModification.setAcceptedByStudent(true);
        contractModification.setAcceptedByCompany(false);
        contractModification.setFinished(false);
        String modificationJsonObject = gson.toJson(contractModification);
        HttpEntity<String> request = requestCreator(modificationJsonObject);
        ContractModification resultedModification =
            restTemplate.postForObject("http://CONTRACT-SERVICE/modification/", request,
                ContractModification.class);
        return resultedModification;
    }

    /**
     * Sends request to the Contract microservice to accept a contractModification as a student.
     *
     * @param studentId      The ID of the student
     * @param modificationId The ID of the modification.
     * @return the modified contract
     */
    public Contract acceptModification(String studentId, Long modificationId) {
        HttpEntity<String> request = requestCreator("{}");
        return restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/student/" + studentId + "/accepts/"
                + modificationId, request, Contract.class);
    }

    /**
     * Sends request to the Contract microservice to decline a contractModification as a student.
     *
     * @param studentId      The ID of the student
     * @param modificationId The ID of the modification.
     * @return the declined modification
     */
    public ContractModification declineModification(String studentId, Long modificationId) {
        HttpEntity<String> request = requestCreator("{}");
        return restTemplate.postForObject(
            "http://CONTRACT-SERVICE/modification/student/" + studentId + "/" + "declines/"
                + modificationId, request, ContractModification.class);
    }

    /**
     * Sends a request to the Contract Microservice to get the proposed modifications from
     * companies.
     *
     * @param studentId the ID of the student
     * @return a List of all the modifications that were proposed by the companies to that student.
     */
    public ModificationResponse getModificationsToRespond(String studentId) {
        List<LinkedHashMap> response =
            restTemplate.getForObject("http://CONTRACT-SERVICE/modification/student/" + studentId,
                List.class);
        List<ContractModification> contractModifications = responseToList(response,
            ContractModification.class);
        Student student = studentRepository.findStudentByNetId(studentId);
        ModificationResponse modificationResponse = new ModificationResponse();
        modificationResponse.setStudent(student);
        modificationResponse.setContractModifications(contractModifications);
        return modificationResponse;
    }

    /**
     * Sends a request to the Contract Microservice to get the all the proposals of modifications
     * in which that student has been involved.
     *
     * @param studentId the ID of the student
     * @return a List of all the modifications
     */
    public ModificationResponse getAllInvolvedInModifications(String studentId) {
        List<LinkedHashMap> response = restTemplate.getForObject(
            "http://CONTRACT-SERVICE/modification/all/student/" + studentId, List.class);
        List<ContractModification> contractModifications = responseToList(response,
            ContractModification.class);
        Student student = studentRepository.findStudentByNetId(studentId);
        ModificationResponse modificationResponse = new ModificationResponse();
        modificationResponse.setStudent(student);
        modificationResponse.setContractModifications(contractModifications);
        return modificationResponse;
    }

    /**
     * Gets a particular contract.
     *
     * @param contractId - the id of the contract.
     * @return the contract that was requested.
     */
    public Contract getContract(Long contractId) {
        return restTemplate.getForObject(
            "http://CONTRACT-SERVICE/contracts/" + contractId, Contract.class);
    }


}
