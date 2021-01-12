package eu.msirbu.tw.tema3.repositories;

import eu.msirbu.tw.tema3.entities.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {
    Employee findEmployeeByEmail(String email);
}
