package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Request")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEmployee")
    private Employee employee;
    @Column(name = "description")
    private String description;
    @Column(name = "startDate", columnDefinition = "DATE")
    private LocalDate startDate;
    @Column(name = "endDate", columnDefinition = "DATE")
    private LocalDate endDate;

    public Request() {
        super();
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

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", employee=" + employee +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}