package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Status;
import eu.msirbu.tw.tema3.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public Optional<Status> getStatusByName(String statusName) {
        return this.statusRepository.findStatusByName(statusName);
    }
}
