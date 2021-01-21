/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.entities.Status;
import eu.msirbu.tw.tema3.services.ApprovalService;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
import eu.msirbu.tw.tema3.services.StatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.*;

@Controller
public class RespondController {

    /* Autowired services */

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;
    private ApprovalService approvalService;
    private StatusService statusService;

    @Autowired
    public void setAuthorizedClientService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Autowired
    public void setApprovalService(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @Autowired
    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    /* Mappings */

    /**
     * "Review your subordinates' requests" endpoint.
     */
    @GetMapping("/review-subordinates")
    public String getReviewSubordinatesPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            Optional<Manager> manager = managerService.getManagerById(employee.get().getId());
            if (!manager.isPresent()) {
                return getNotAManagerErrorPage(model);
            }
            List<Approval> approvals = manager.get().getApprovals();
            approvals.sort(Collections.reverseOrder());
            model.addAttribute("approvals", approvals);
            return "review-subordinates";
        }
        return getNotEnrolledErrorPage(model);
    }

    /**
     * "Approve request with ID" endpoint
     */
    @GetMapping(path = "/respond/approve/{id}")
    public String getRespondApprovePage(@PathVariable(value = "id") int approvalId, Model model, OAuth2AuthenticationToken authenticationToken) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") final Status APPROVED = statusService.getStatusByName("APPROVED").get();
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            Optional<Approval> approval = approvalService.getApprovalById(approvalId);
            Optional<Manager> manager = managerService.getManagerById(employee.get().getId());
            if (!manager.isPresent()) {
                return getNotAManagerErrorPage(model);
            }
            if (!approval.isPresent()) {
                return getApprovalNotFoundErrorPage(model);
            }
            if (approval.get().getManager().getId() != manager.get().getId()) {
                return getApprovalNotSuperiorOfRequesterErrorPage(model);
            }
            if (!approval.get().getStatus().equals(new Status("PENDING"))) {
                return getResponseSentDuplicateErrorPage(model);
            }
            approvalService.updateApprovalStatus(approval.get(), APPROVED);
            return getResponseSentSuccessPage(model);
        }
        return getNotEnrolledErrorPage(model);
    }

    /**
     * "Decline request with ID" endpoint
     */
    @GetMapping(path = "/respond/decline/{id}")
    public String getRespondDeclinePage(@PathVariable(value = "id") int approvalId, Model model, OAuth2AuthenticationToken authenticationToken) {
        @SuppressWarnings("OptionalGetWithoutIsPresent") final Status DECLINED = statusService.getStatusByName("DECLINED").get();
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            Optional<Approval> approval = approvalService.getApprovalById(approvalId);
            Optional<Manager> manager = managerService.getManagerById(employee.get().getId());
            if (!manager.isPresent()) {
                return getNotAManagerErrorPage(model);
            }
            if (!approval.isPresent()) {
                return getApprovalNotFoundErrorPage(model);
            }
            if (approval.get().getManager().getId() != manager.get().getId()) {
                return getApprovalNotSuperiorOfRequesterErrorPage(model);
            }
            if (!approval.get().getStatus().equals(new Status("PENDING"))) {
                return getResponseSentDuplicateErrorPage(model);
            }
            approvalService.updateApprovalStatus(approval.get(), DECLINED);
            return getResponseSentSuccessPage(model);
        }
        return getNotEnrolledErrorPage(model);
    }

    @GetMapping(path = "/respond/{id}")
    public String getRespondPage(@PathVariable(value = "id") int approvalId, Model model, OAuth2AuthenticationToken authenticationToken) {
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            Optional<Approval> approval = approvalService.getApprovalById(approvalId);
            if (!approval.isPresent()) {
                return getApprovalNotFoundErrorPage(model);
            }
            Optional<Manager> manager = managerService.getManagerById(employee.get().getId());
            if (!manager.isPresent()) {
                return getNotAManagerErrorPage(model);
            }
            if (approval.get().getManager().getId() != manager.get().getId()) {
                return getApprovalNotSuperiorOfRequesterErrorPage(model);
            }
            model.addAttribute("approval", approval.get());
            model.addAttribute("request", approval.get().getRequest());
            model.addAttribute("allApprovals", approval.get().getRequest().getApprovals());
            model.addAttribute("manager", approval.get().getManager());
            model.addAttribute("employee", approval.get().getManager().getEmployee());
            return "respond";
        }
        return getNotEnrolledErrorPage(model);
    }
}
