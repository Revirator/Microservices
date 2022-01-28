package nl.tudelft.sem.request.repository;

import java.util.List;
import nl.tudelft.sem.request.entity.StudentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRequestRepository extends JpaRepository<StudentRequest, Long> {
    StudentRequest findByStudentId(String studentId);

    StudentRequest findByServiceId(Long serviceId);

    List<StudentRequest> findAllByHoursPerWeekBetween(int begin, int hours);
}
