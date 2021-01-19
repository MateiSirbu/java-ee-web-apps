package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Status;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.repositories.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getAllStatuses() {
        List<Status> statusList = new ArrayList<>();
        this.statusRepository.findAll().forEach(statusList::add);
        return statusList;
    }

    public Status getStatusById(int statusId) throws NotFoundException {
        return this.statusRepository
                .findById(statusId)
                .orElseThrow(() -> new NotFoundException("Cannot find Status with id " + statusId + "."));
    }

    public Status getStatusByName(String statusName) throws NotFoundException {
        return this.statusRepository
                .findStatusByName(statusName)
                .orElseThrow(() -> new NotFoundException("Cannot find Status with name " + statusName + "."));
    }
}
