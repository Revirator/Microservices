package nl.tudelft.sem.request.controller;

import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.CompanyRequestModification;
import nl.tudelft.sem.request.service.ModificationService;
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
public class ModificationController {

    private final ModificationService modificationService;

    @Autowired
    public ModificationController(ModificationService modificationService) {
        this.modificationService = modificationService;
    }

    /**
     * Save the changes that a student has requested to a database.
     *
     * @param serviceId the ID of the service.
     * @param companyRequestModification the requested changes.
     * @return the CompanyRequestModification.
     */
    @PostMapping("/{serviceId}")
    @ResponseBody
    public CompanyRequestModification
        saveModification(@PathVariable Long serviceId,
                         @RequestBody CompanyRequestModification companyRequestModification) {
        return modificationService.saveModification(serviceId, companyRequestModification);
    }

    /**
     * Return all modifications proposed by students for
     * the given company.
     *
     * @param companyId the ID of the company.
     * @return a list of all modifications.
     */
    @GetMapping("company/{companyId}/all")
    @ResponseBody
    public List<CompanyRequestModification>
        getAllModificationsForCompany(@PathVariable String companyId) {
        return modificationService.getAllModificationsForCompany(companyId);
    }

    /**
     * Accept the changes suggested by the student and apply them
     * to the job request.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification.
     * @return the updated CompanyRequest.
     */
    @PostMapping("/company/{companyId}/accepts/{modificationId}")
    @ResponseBody
    public CompanyRequest acceptChanges(@PathVariable String companyId,
                                        @PathVariable Long modificationId) {
        return modificationService.acceptChanges(companyId, modificationId);
    }

    /**
     * Reject the changes suggested by the student and also
     * delete the modification request.
     *
     * @param companyId the ID of the company.
     * @param modificationId the ID of the modification .
     * @return the original CompanyRequest.
     */
    @PostMapping("/company/{companyId}/rejects/{modificationId}")
    @ResponseBody
    public CompanyRequest rejectChanges(@PathVariable String companyId,
                                        @PathVariable Long modificationId) {
        return modificationService.rejectChanges(companyId, modificationId);
    }
}
