package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "Approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "idRequest")
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

    @Override
    public String toString() {
        return "Approval{" +
                "id=" + id +
                ", request=" + request +
                ", manager=" + manager +
                ", status=" + status +
                '}';
    }
}