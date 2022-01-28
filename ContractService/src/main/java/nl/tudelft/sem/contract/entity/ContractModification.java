package nl.tudelft.sem.contract.entity;

import static javax.persistence.GenerationType.SEQUENCE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractModification {
    @Id
    @SequenceGenerator(name = "user_sequence", sequenceName = "user_sequence", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "user_sequence")
    private Long modificationId;

    private String modificationType;

    private Long contractId;

    private String studentId;

    private String companyId;

    private int hoursPerWeek;

    private int totalHours;

    private double pricePerHour;

    private boolean acceptedByCompany;

    private boolean acceptedByStudent;

    private boolean finished;

    /**
     * Constructor used for testing purposes.
     *
     * @param modificationType type of modification
     * @param contractId the id of the contract that should be modified
     * @param studentId the id of the student implicated
     * @param companyId the id of the company implicated
     * @param hoursPerWeek the hours per week
     * @param totalHours the total hours
     * @param pricePerHour the price per hours
     * @param acceptedByCompany if the modification is accepted by the company
     * @param acceptedByStudent if the modification is accepted by the student
     * @param finished if the modification is finished
     */
    public ContractModification(String modificationType, Long contractId, String studentId,
                                String companyId, int hoursPerWeek, int totalHours,
                                double pricePerHour, boolean acceptedByCompany,
                                boolean acceptedByStudent, boolean finished) {
        this.modificationType = modificationType;
        this.contractId = contractId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
        this.acceptedByCompany = acceptedByCompany;
        this.acceptedByStudent = acceptedByStudent;
        this.finished = finished;
    }

}

