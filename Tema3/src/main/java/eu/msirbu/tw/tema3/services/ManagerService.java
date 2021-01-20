package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public Optional<Manager> getManagerById(int employeeId) {
        return this.managerRepository.findById(employeeId);
    }


}
