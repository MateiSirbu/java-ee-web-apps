package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.Team;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TeamRepository extends CrudRepository<Team, Integer> {
    Optional<Team> findTeamByName(String name);
}