package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Team;
import eu.msirbu.tw.tema3.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<Team> getAllTeams() {
        List<Team> teamList = new ArrayList<>();
        this.teamRepository.findAll().forEach(teamList::add);
        return teamList;
    }

    public Optional<Team> getTeamById(int statusId)  {
        return this.teamRepository.findById(statusId);
    }

    public Optional<Team> getTeamByName(String statusName)  {
        return this.teamRepository.findTeamByName(statusName);
    }
}
