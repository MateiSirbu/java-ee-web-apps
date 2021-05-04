/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.services;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Status;
import eu.msirbu.tw.tema3.repositories.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApprovalService {
    private final ApprovalRepository approvalRepository;

    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
    }

    public Optional<Approval> getApprovalById(int approvalId) {
        return this.approvalRepository.findById(approvalId);
    }

    public void updateApprovalStatus(Approval approval, Status status) {
        approval.setStatus(status);
        approvalRepository.save(approval);
    }
}
