package nl.tudelft.sem.request.repository;

import java.util.List;
import nl.tudelft.sem.request.entity.CompanyRequest;
import nl.tudelft.sem.request.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRequestRepository extends JpaRepository<CompanyRequest, Long> {

    List<CompanyRequest> findAllByCompanyId(String companyId);

    CompanyRequest findByServiceId(Long serviceId);

    List<CompanyRequest> findAllByHoursPerWeekBetween(int startOfInterval, int endOfInterval);

    List<CompanyRequest> findAllByTotalHoursBetween(int startOfInterval, int endOfInterval);

    List<CompanyRequest> findAllBySalaryPerHourBetween(double startOfInterval,
                                                       double endOfInterval);

    List<CompanyRequest> findCompanyRequestsByTargetStudentId(String studentId);

}
