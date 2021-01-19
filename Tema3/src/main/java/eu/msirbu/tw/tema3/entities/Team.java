package eu.msirbu.tw.tema3.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Team")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @OneToOne
    @JoinColumn(name = "idLeader")
    private Manager leader;
    @ManyToMany(mappedBy = "teams")
    private List<Employee> members;

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

    public List<Employee> getMembers() {
        return members;
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

    public void setMembers(List<Employee> members) {
        this.members = members;
    }

}
