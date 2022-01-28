package nl.tudelft.sem.request.repository;

import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.Request;
import nl.tudelft.sem.request.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository<T extends Request> extends JpaRepository<T, Long> {

    List<CompanyRequest> findAllByCompanyId(String companyId);

    List<StudentRequest> findAllByStudentId(String studentId);

    T findByServiceId(Long serviceId);
}
