package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Request")
public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmployee")
    private Employee employee;

    @NotNull
    @Column(name = "startDate", columnDefinition = "DATE")
    private LocalDate startDate;

    @NotNull
    @Column(name = "endDate", columnDefinition = "DATE")
    private LocalDate endDate;

    @OneToMany(mappedBy="request", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    public Employee getEmployee() {
        return employee;
    }

    public List<Approval> getApprovals() {
        return approvals;
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
}