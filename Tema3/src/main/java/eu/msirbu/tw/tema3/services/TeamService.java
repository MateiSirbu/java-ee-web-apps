package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Team;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

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

    public Team getTeamById(int statusId) throws NotFoundException {
        return this.teamRepository
                .findById(statusId)
                .orElseThrow(() -> new NotFoundException("Cannot find Team with id " + statusId + "."));
    }

    public Team getTeamByName(String statusName) throws NotFoundException {
        return this.teamRepository
                .findTeamByName(statusName)
                .orElseThrow(() -> new NotFoundException("Cannot find Team with name " + statusName + "."));
    }
}
