package eu.msirbu.tw.tema3.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name = "Approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idRequest", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Request request;

    @ManyToOne
    @JoinColumn(name = "idManager")
    private Employee manager;

    @ManyToOne
    @JoinColumn(name="idStatus")
    private Status status;

    public Approval() {
        super();
    }

    public Approval(Employee manager, Status status) {
        this.manager = manager;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Employee getManager() {
        return manager;
    }

    public Request getRequest() {
        return request;
    }

    public Status getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setManager(Employee manager) {
        this.manager = manager;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}