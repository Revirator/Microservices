package nl.tudelft.sem.request.repository;

import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequestModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRequestModificationRepository
        extends JpaRepository<CompanyRequestModification, Long> {

    List<CompanyRequestModification> findAllByCompanyId(String companyId);
}
