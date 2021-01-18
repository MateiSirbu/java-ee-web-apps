package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "Manager")
public class Manager {
    @Id
    @Column(name = "idEmployee")
    private int id;
    @OneToOne
    @JoinColumn(name = "idEmployee")
    private Employee employee;
    @ManyToOne
    @JoinColumn(name = "idSuperior")
    private Manager superior;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setSuperior(Manager superior) {
        this.superior = superior;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "id=" + id +
                ", employee=" + employee +
                ", superior=" + superior +
                '}';
    }
}
