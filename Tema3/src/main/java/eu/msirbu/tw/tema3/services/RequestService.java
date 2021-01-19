package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Request;
import eu.msirbu.tw.tema3.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public int addRequest(Request request) {
        return requestRepository.save(request).getId();
    }

    public List<Request> getAllRequests() {
        List<Request> requestList = new ArrayList<>();
        this.requestRepository.findAll().forEach(requestList::add);
        return requestList;
    }
}
