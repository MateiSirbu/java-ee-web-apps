package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerService {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public List<Manager> getAllManagers() {
        List<Manager> managerList = new ArrayList<>();
        this.managerRepository.findAll().forEach(managerList::add);
        return managerList;
    }

    public Manager getManagerById(int employeeId) throws NotAManagerException {
        return this.managerRepository
                .findById(employeeId)
                .orElseThrow(() -> new NotAManagerException("You cannot access this resource because you are not a manager."));
    }


}
