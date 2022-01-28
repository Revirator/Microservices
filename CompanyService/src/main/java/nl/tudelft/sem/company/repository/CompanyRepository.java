package nl.tudelft.sem.company.repository;

import nl.tudelft.sem.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    Company findCompanyByUsername(String username);
}
