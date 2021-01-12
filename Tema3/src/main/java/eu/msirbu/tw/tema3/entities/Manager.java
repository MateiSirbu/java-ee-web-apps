package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "manager")
public class Manager {
    @Id
    @Column(name = "id_employee")
    private int id;
    @OneToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;
    @OneToOne
    @JoinColumn(name = "id_superior")
    private Employee superior;

    public Manager() {
        super();
    }

    public int getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Employee getSuperior() {
        return superior;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setSuperior(Employee superior) {
        this.superior = superior;
    }
}
