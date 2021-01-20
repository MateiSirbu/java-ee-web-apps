package eu.msirbu.tw.tema3.entities;

import eu.msirbu.tw.tema3.services.PublicHolidayService;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import static eu.msirbu.tw.tema3.controllers.utils.Utils.countExemptDays;

@Entity
@Table(name = "Employee")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "givenName")
    private String givenName;

    @Column(name = "familyName")
    private String familyName;

    @Column(name = "email")
    private String email;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "employee")
    private List<Request> requests;

    @Column(name = "vacationDayQuota")
    private int vacationDayQuota;

    @ManyToMany
    @JoinTable(name = "EmployeeTeam",
            joinColumns = @JoinColumn(name = "idEmployee"),
            inverseJoinColumns = @JoinColumn(name = "idTeam"))
    private List<Team> teams;

    public Employee() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFullName() {
        return givenName + " " + familyName;
    }

    public String getFullNameAndEmail() {
        return getFullName() + " (" + email + ") ";
    }

    public List<Request> getRequests() {
        return requests;
    }

    public List<Manager> getSuperiors() {
        List<Manager> superiors = new ArrayList<>();
        for (Team team : teams) {
            Manager leader = team.getLeader();
            if (leader.getId() != id)
                superiors.add(team.getLeader());
        }
        return superiors;
    }

    public int getApprovedDays(PublicHolidayService publicHolidayService) {
        int daysInInterval = 0;
        for (Request request : requests) {
            if (request.getAggregatedStatus().equals(new Status("APPROVED"))) {
                LocalDate start = request.getStartDate();
                LocalDate end = request.getEndDate();
                daysInInterval += Period.between(start, end.plusDays(1)).getDays() - countExemptDays(start, end, publicHolidayService);
            }
        }
        return daysInInterval;
    }

    public int getPendingDays(PublicHolidayService publicHolidayService) {
        int daysInInterval = 0;
        for (Request request : requests) {
            if (request.getAggregatedStatus().equals(new Status("PENDING"))) {
                LocalDate start = request.getStartDate();
                LocalDate end = request.getEndDate();
                daysInInterval += Period.between(start, end.plusDays(1)).getDays() - countExemptDays(start, end, publicHolidayService);
            }
        }
        return daysInInterval;
    }

    public int getRemainingVacationDays(PublicHolidayService publicHolidayService) {
        return vacationDayQuota - getApprovedDays(publicHolidayService);
    }

    public int getRequestableVacationDays(PublicHolidayService publicHolidayService) {
        return getRemainingVacationDays(publicHolidayService) - getPendingDays(publicHolidayService);
    }

    public int getVacationDayQuota() {
        return vacationDayQuota;
    }

    public List<Team> getTeams() {
        return teams;
    }

    private List<String> getTeamNames() {
        List<String> teamNames = new ArrayList<>();
        for (Team team : teams) {
            teamNames.add(team.getName());
        }
        return teamNames;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRequests(List<Request> requests) {
        this.requests = requests;
    }

    public void setVacationDayQuota(int vacationDayQuota) {
        this.vacationDayQuota = vacationDayQuota;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

}
