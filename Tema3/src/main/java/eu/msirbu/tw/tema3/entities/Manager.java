/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Manager")
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idEmployee")
    private int id;

    @OneToOne
    @JoinColumn(name = "idEmployee")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "idSuperior")
    private Manager superior;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "manager")
    private List<Approval> approvals;

    public Manager() {
        super();
    }

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Manager getSuperior() {
        return superior;
    }

    public List<Approval> getApprovals() {
        return approvals;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setSuperior(Manager superior) {
        this.superior = superior;
    }

    public void setApprovals(List<Approval> approvals) {
        this.approvals = approvals;
    }
}
