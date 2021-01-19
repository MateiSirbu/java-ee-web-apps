package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StatusRepository extends CrudRepository<Status, Integer> {
    Optional<Status> findStatusByName(String name);
}