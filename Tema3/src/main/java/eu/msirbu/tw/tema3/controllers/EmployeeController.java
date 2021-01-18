package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/db/employees")
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employeeList::add);
        return employeeList;
    }

    @GetMapping(path = "/db/employee/{id}")
    public Employee getEmployeeById(@PathVariable(value = "id") int employeeId) throws NotFoundException {
        return this.employeeRepository
                .findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Cannot find Employee with id " + employeeId + "."));
    }

    @GetMapping(path = "/db/employee")
    public Employee getEmployeeByEmail(@RequestParam(value = "email") String employeeEmail) throws NotFoundException {
        return this.employeeRepository
                .findEmployeeByEmail(employeeEmail)
                .orElseThrow(() -> new NotFoundException("Cannot find Employee with email " + employeeEmail + "."));
    }
}