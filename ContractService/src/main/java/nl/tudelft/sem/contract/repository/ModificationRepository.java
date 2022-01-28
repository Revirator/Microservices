package nl.tudelft.sem.contract.repository;

import java.util.List;
import nl.tudelft.sem.contract.entity.ContractModification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModificationRepository extends JpaRepository<ContractModification, Long> {

    List<ContractModification> findAllByStudentIdAndAcceptedByCompanyAndFinished(
        String studentId,
        Boolean acceptedByCompany,
        Boolean finished
    );

    List<ContractModification> findAllByCompanyIdAndAcceptedByStudentAndFinished(
        String companyId,
        Boolean acceptedByStudent,
        Boolean finished
    );

    List<ContractModification> findAllByCompanyId(String companyId);

    List<ContractModification> findAllByStudentId(String studentId);

}
