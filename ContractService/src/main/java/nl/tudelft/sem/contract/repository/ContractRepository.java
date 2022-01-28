package nl.tudelft.sem.contract.repository;

import java.util.List;
import nl.tudelft.sem.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Contract findContractByContractId(String contractId);

    List<Contract> findAllByStudentId(String studentId);

    List<Contract> findAllByCompanyId(String companyId);
}
