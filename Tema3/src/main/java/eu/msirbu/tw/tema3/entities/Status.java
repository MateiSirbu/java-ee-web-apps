package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;

    public Status() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String name) {
        this.name = name;
    }
}

