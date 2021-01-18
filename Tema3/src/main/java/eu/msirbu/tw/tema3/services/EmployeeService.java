package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employeeList::add);
        return employeeList;
    }

    public Employee getEmployeeById(int employeeId) throws NotFoundException {
        return this.employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Cannot find Employee with id " + employeeId + "."));
    }

    public Employee getEmployeeByEmail(String employeeEmail) throws NotFoundException {
        return this.employeeRepository
                .findEmployeeByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Cannot find Employee with email " + employeeEmail + "."));
    }
}
