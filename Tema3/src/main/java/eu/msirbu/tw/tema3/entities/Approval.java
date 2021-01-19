package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Approval")
public class Approval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade=CascadeType.ALL)
    @JoinColumn(name = "idRequest")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "idManager")
    private Manager manager;

    @ManyToOne
    @JoinColumn(name="idStatus")
    private Status status;

    public Approval() {
        super();
    }

    public Approval(Request request, Manager manager, Status status) {
        this.request = request;
        this.manager = manager;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Manager getManager() {
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

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

}