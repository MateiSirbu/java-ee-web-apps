package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "Request")
public class Request implements Serializable, Comparable<Request> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idEmployee")
    private Employee employee;

    @NotNull
    @Column(name = "startDate", columnDefinition = "DATE")
    private LocalDate startDate;

    @NotNull
    @Column(name = "endDate", columnDefinition = "DATE")
    private LocalDate endDate;

    @OneToMany(mappedBy = "request", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Approval> approvals;

    public Request() {
        super();
    }

    public Request(Employee employee, LocalDate startDate, LocalDate endDate) {
        this.employee = employee;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getFormattedStartDate() {
        return startDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
    }

    public String getFormattedEndDate() {
        return endDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
    }

    public Employee getEmployee() {
        return employee;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public Status getAggregatedStatus() {
        final Status PENDING = new Status("PENDING");
        final Status APPROVED = new Status("APPROVED");
        final Status DECLINED = new Status("DECLINED");
        Status aggregatedStatus = APPROVED;
        for (Approval approval : approvals) {
            if (approval.getStatus().equals(PENDING)) {
                if (startDate.isBefore(LocalDate.now().plus(2, ChronoUnit.DAYS))) {
                    aggregatedStatus = DECLINED;
                    break;
                } else {
                    aggregatedStatus = PENDING;
                }
            } else if (approval.getStatus().equals(DECLINED)) {
                aggregatedStatus = DECLINED;
                break;
            }
        }
        return aggregatedStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setIdEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }

    @Override
    public int compareTo(Request o) {
        return getStartDate().compareTo(o.getStartDate());
    }
}