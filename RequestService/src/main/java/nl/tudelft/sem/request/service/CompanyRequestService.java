package nl.tudelft.sem.request.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import nl.tudelft.sem.request.valueobjects.CompanyRequestChange;
import nl.tudelft.sem.request.valueobjects.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompanyRequestService extends RequestService {

    @Autowired
    private CompanyRequestRepository companyRequestRepository;

    /**
     * Receives the new companyRequest
     * and forwards it to the repository.
     *
     * @param companyRequest the new companyRequest made by either Company
     * @return new CompanyRequest Instance
     */
    public CompanyRequest saveCompanyRequest(CompanyRequest companyRequest) {
        return companyRequestRepository.save(companyRequest);
    }

    /**
     * Used to get all the company requests.
     *
     * @return a list of all the company requests in the database
     */
    public List<CompanyRequest> getAllCompanyRequests() {
        return companyRequestRepository.findAll();

    }

    /**
     * Used to get all the company requests for a specific company.
     *
     * @param companyId the id of that specific company
     * @return a list of all the requests for that specific company in the database
     */
    public List<CompanyRequest> findAllRequestByCompanyId(String companyId) {
        return companyRequestRepository.findAllByCompanyId(companyId);
    }

    /**
     * Used to get all the company requests filtered by hoursPerWeek.
     * With a specific interval.
     *
     * @param startOfInterval the start of that specific interval
     * @param endOfInterval   the end of that specific interval
     * @return a list of all the requests for that specific filter
     */
    public List<CompanyRequest> findAllCompanyRequestsByHoursPerWeek(int startOfInterval,
                                                                     int endOfInterval) {
        return companyRequestRepository.findAllByHoursPerWeekBetween(startOfInterval,
            endOfInterval);
    }

    /**
     * Used to get all the company requests filtered by salary per hour.
     * With a specific interval.
     *
     * @param startOfInterval the start of that specific interval
     * @param endOfInterval   the end of that specific interval
     * @return a list of all the requests for that specific filter
     */
    public List<CompanyRequest> findAllCompanyRequestsBySalaryPerHour(int startOfInterval,
                                                                      int endOfInterval) {
        return companyRequestRepository.findAllBySalaryPerHourBetween(startOfInterval,
            endOfInterval);
    }

    /**
     * Used to get all the company requests filtered by total hours.
     * With a specific interval.
     *
     * @param startOfInterval the start of that specific interval
     * @param endOfInterval   the end of that specific interval
     * @return a list of all the requests for that specific filter
     */
    public List<CompanyRequest> findAllCompanyRequestsByTotalHours(int startOfInterval,
                                                                   int endOfInterval) {
        return companyRequestRepository.findAllByTotalHoursBetween(startOfInterval, endOfInterval);
    }

    /**
     * Deletes all job requests from the database from specified company.
     *
     * @param companyId - the Id of the company who made the service request.
     * @return the list of deleted job requests.
     */
    public List<CompanyRequest> deleteAllCompanyRequests(String companyId) {
        List<CompanyRequest> requests = companyRequestRepository.findAllByCompanyId(companyId);
        int i = 0;
        int end = requests.size();
        while (i < end) {
            companyRequestRepository.delete(requests.get(i));
            i++;
        }
        return requests;
    }

    /**
     * Deletes certain job requests from the database from specified company.
     *
     * @param serviceId - the Id of the service that will be deleted.
     * @return the list of deleted job requests.
     */
    public CompanyRequest deleteCompanyRequest(Long serviceId) {
        CompanyRequest request = companyRequestRepository.findByServiceId(serviceId);
        if (request != null) {
            companyRequestRepository.delete(request);
        }
        return request;
    }

    /**
     * Updates the parameters of a student's request.
     *
     * @param serviceId - the id of the service.
     * @param requested - the changes to the request data.
     * @return the updated request.
     */
    public CompanyRequest updateCompanyRequest(Long serviceId, CompanyRequestChange requested) {
        CompanyRequest request = companyRequestRepository.findByServiceId(serviceId);
        if (request == null) {
            return null;
        }
        if (requested.getRequirements() != null) {
            request.setRequirements(requested.getRequirements());
        }
        if (requested.getTotalHours() != -1) {
            request.setTotalHours(requested.getTotalHours());
        }
        if (requested.getSalaryPerHour() != -1) {
            request.setSalaryPerHour(requested.getSalaryPerHour());
        }
        if (requested.getHoursPerWeek() != -1) {
            request.setHoursPerWeek(requested.getHoursPerWeek());
        }
        companyRequestRepository.save(request);
        return companyRequestRepository.findByServiceId(serviceId);
    }

    /**
     * Accepts a request made by a company as a student.
     *
     * @param studentId The ID of the student accepting the companyRequest
     * @param serviceId The ID of the service to be accepted.
     * @return The CompanyRequest that is accepted.
     */
    public CompanyRequest acceptCompanyRequest(String studentId, Long serviceId) {
        CompanyRequest r = companyRequestRepository.findByServiceId(serviceId);
        // Since the studentID field will not be null now, it indicates the company request has
        // been accepted by a student.
        r.addStudentId(studentId);
        return companyRequestRepository.saveAndFlush(r);
    }

    /**
     * Returns the candidates for students who have accepted that company request.
     *
     * @param serviceId The ID of the service.
     * @return A list of student ID's for the company to choose from.
     */
    public List<String> getStudentIdCandidates(Long serviceId) {
        CompanyRequest r = companyRequestRepository.findByServiceId(serviceId);
        return r.getStudentIdCandidates();
    }


    /**
     * The company accepts its own request that it put up, assuming a student also accepted the
     * terms and then a contract is generated.
     *
     * @param companyId The ID of the company
     * @param serviceId The ID of the request service.
     * @return The company request
     */
    public CompanyRequest acceptOwnRequestCompany(String companyId, String studentId,
                                                  Long serviceId) {
        CompanyRequest r = companyRequestRepository.findByServiceId(serviceId);

        r.setAcceptedByCompany(true);
        r.setStudentId(studentId);

        //Generate contract
        Contract contract = new Contract();
        contract.setHoursPerWeek(r.getHoursPerWeek());
        contract.setTotalHours(r.getTotalHours());
        contract.setPricePerHour(r.getSalaryPerHour());
        contract.setCompanyId(r.getCompanyId());
        contract.setStudentId(studentId);

        //Send contract to contract microservice
        super.sendGeneratedContract(contract);
        companyRequestRepository.delete(r);
        return r;
    }

    /**
     * Rejects the student as a company (The company does not want to work with a certain student).
     *
     * @param serviceId The ID of the service
     * @return The rejected CompanyRequest
     */
    public CompanyRequest rejectStudentAsCompany(Long serviceId, String studentId) {
        CompanyRequest r = companyRequestRepository.findByServiceId(serviceId);
        // Since the studentID field will be null now, it indicates the company rejected the
        // student
        r.removeStudentId(studentId);
        r.setAcceptedByCompany(false);
        return companyRequestRepository.saveAndFlush(r);
    }

    /**
     * Finds a Company Request by service ID.
     *
     * @param serviceId The ID of the service/request.
     * @return The Company Request
     */
    public CompanyRequest findCompanyRequestByServiceId(Long serviceId) {
        return companyRequestRepository.findByServiceId(serviceId);
    }

    /**
     * Receives a request from the controller, and passes it on to the repository.
     * Returns a list of company requests where a student was specifically targeted
     *
     * @param studentId the student to search for
     * @return a list of company requests that targeted the student
     */
    public List<CompanyRequest> findTargetedRequestsForStudent(String studentId) {
        return companyRequestRepository.findCompanyRequestsByTargetStudentId(studentId);
    }

    /**
     * Receives a request from the controller, and passes it to the repository.
     * Returns the request the student was targeted with.
     *
     * @param companyRequest the request the student was targeted with.
     * @param studentId      the student targeted with the request.
     * @return the request the student was targeted with.
     */
    public CompanyRequest saveTargetedCompanyRequest(CompanyRequest companyRequest,
                                                     String studentId) {
        companyRequest.setTargetStudentId(studentId);
        return companyRequestRepository.save(companyRequest);
    }

}
