package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    Optional<Employee> findEmployeeByEmail(String email);
}
