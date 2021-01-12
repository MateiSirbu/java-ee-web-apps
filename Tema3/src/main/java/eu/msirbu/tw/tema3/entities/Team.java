package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;

@Entity
@Table(name = "team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @OneToOne
    @JoinColumn(name = "id_leader")
    private Manager leader;

    public Team() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Manager getLeader() {
        return leader;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeader(Manager leader) {
        this.leader = leader;
    }
}
