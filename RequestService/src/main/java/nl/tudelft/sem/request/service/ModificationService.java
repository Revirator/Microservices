package nl.tudelft.sem.request.service;

import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.CompanyRequestModification;
import nl.tudelft.sem.request.repository.CompanyRequestModificationRepository;
import nl.tudelft.sem.request.repository.CompanyRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModificationService {

    @Autowired
    private CompanyRequestRepository companyRequestRepository;
    @Autowired
    private CompanyRequestModificationRepository companyRequestModificationRepository;

    /**
     * Save the changes that a student has requested to a database.
     *
     * @param serviceId the ID of the service.
     * @param companyRequestModification the requested changes.
     * @return the CompanyRequestModification.
     */
    public CompanyRequestModification
        saveModification(Long serviceId,
                         CompanyRequestModification companyRequestModification) {
        String companyId = companyRequestRepository.getOne(serviceId).getCompanyId();
        companyRequestModification.setCompanyId(companyId);
        return companyRequestModificationRepository.save(companyRequestModification);
    }

    /**
     * Return all modifications proposed by students for
     * the given company.
     *
     * @param companyId the ID of the company.
     * @return a list of all modifications.
     */
    public List<CompanyRequestModification> getAllModificationsForCompany(String companyId) {
        return companyRequestModificationRepository.findAllByCompanyId(companyId);
    }

    /**
     * Accept the changes suggested by the student and apply them
     * to the job request.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the updated CompanyRequest.
     */
    public CompanyRequest acceptChanges(String companyId, Long modificationId) {
        CompanyRequestModification modification = companyRequestModificationRepository
                .getOne(modificationId);
        CompanyRequest companyRequest = companyRequestRepository
                .getOne(modification.getServiceId());
        companyRequestRepository.delete(companyRequest);
        if (modification.getHoursPerWeek() > 0) {
            companyRequest.setHoursPerWeek(modification.getHoursPerWeek());
        }
        if (modification.getTotalHours() > 0) {
            companyRequest.setTotalHours(modification.getTotalHours());
        }
        if (modification.getPricePerHour() > 0) {
            companyRequest.setSalaryPerHour(modification.getPricePerHour());
        }
        return companyRequestRepository.save(companyRequest);
    }

    /**
     * Reject the changes suggested by the student and also
     * delete the modification request.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the original CompanyRequest.
     */
    public CompanyRequest rejectChanges(String companyId, Long modificationId) {
        CompanyRequestModification modification = companyRequestModificationRepository
                .getOne(modificationId);
        companyRequestModificationRepository.deleteById(modificationId);
        return companyRequestRepository.getOne(modification.getServiceId());
    }
}
