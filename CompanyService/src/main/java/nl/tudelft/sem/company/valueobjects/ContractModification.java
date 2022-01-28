package nl.tudelft.sem.company.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractModification {
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
     * Constructor without id.
     *
     * @param modificationType  type of modification
     * @param contractId        the id of the contract that should be modified
     * @param studentId         the id of the student implicated
     * @param companyId         the id of the company implicated
     * @param hoursPerWeek      the hours per week
     * @param totalHours        the total hours
     * @param pricePerHour      the price per hours
     * @param acceptedByCompany if the modification is accepted by the company
     * @param acceptedByStudent if the modification is accepted by the student
     * @param finished          if the modification is finished
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

    /**
     * Constructor for company.
     *
     * @param modificationType type of modification
     * @param contractId       the id of the contract that should be modified
     * @param studentId        the id of the student implicated
     * @param companyId        the id of the company implicated
     * @param hoursPerWeek     the hours per week
     * @param totalHours       the total hours
     * @param pricePerHour     the price per hours
     */
    public ContractModification(String modificationType, Long contractId, String studentId,
                                String companyId, int hoursPerWeek, int totalHours,
                                double pricePerHour) {
        this.modificationType = modificationType;
        this.contractId = contractId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.hoursPerWeek = hoursPerWeek;
        this.totalHours = totalHours;
        this.pricePerHour = pricePerHour;
    }
}
