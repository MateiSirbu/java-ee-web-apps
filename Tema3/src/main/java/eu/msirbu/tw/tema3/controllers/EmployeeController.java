package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        List<Employee> employeeList = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employeeList::add);
        return employeeList;
    }

    @GetMapping("/employee/{id}")
    public List<Employee> getEmployeeById() {
        List<Employee> employeeList = new ArrayList<>();
        this.employeeRepository.
        return employeeList;
    }

    @GetMapping("/employee/{email}")
    public List<Employee> getEmployeeByEmail() {
        List<Employee> employeeList = new ArrayList<>();
        this.employeeRepository.findAll().forEach(employeeList::add);
        return employeeList;
    }

}
