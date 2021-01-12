package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "approval")
public class Approval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne
    @JoinColumn(name = "id_request")
    private Request request;
    @ManyToOne
    @JoinColumn(name = "id_manager")
    private Employee manager;
    @ManyToOne
    @JoinColumn(name="id_status")
    private Status status;

    public Approval() {
        super();
    }
}