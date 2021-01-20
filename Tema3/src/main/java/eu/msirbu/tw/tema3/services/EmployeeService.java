package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Optional<Employee> getEmployeeByEmail(String employeeEmail) {
        return this.employeeRepository.findEmployeeByEmail(employeeEmail);
    }
}
