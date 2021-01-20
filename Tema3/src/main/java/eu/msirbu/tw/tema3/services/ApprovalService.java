package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Request;
import eu.msirbu.tw.tema3.repositories.ApprovalRepository;
import eu.msirbu.tw.tema3.repositories.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApprovalService {
    private final ApprovalRepository approvalRepository;

    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    public int addApproval(Approval approval) {
        return approvalRepository.save(approval).getId();
    }

    public List<Approval> getAllApprovals() {
        List<Approval> approvalList = new ArrayList<>();
        this.approvalRepository.findAll().forEach(approvalList::add);
        return approvalList;
    }

    public Optional<Approval> getApprovalById(int approvalId) {
        return this.approvalRepository.findById(approvalId);
    }
}
