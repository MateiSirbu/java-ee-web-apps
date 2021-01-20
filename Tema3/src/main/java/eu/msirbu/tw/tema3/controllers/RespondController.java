package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.ApprovalService;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
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

import static eu.msirbu.tw.tema3.controllers.utils.Utils.*;

@Controller
public class RespondController {

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;
    private ApprovalService approvalService;

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


    @GetMapping("/review-subordinates")
    public String getReviewSubordinatesPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            Manager manager = managerService.getManagerById(employee.getId());
            List<Approval> approvals = manager.getApprovals();
            approvals.sort(Collections.reverseOrder());
            model.addAttribute("approvals", approvals);
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        }
        catch (NotAManagerException e) {
            return getNotAManagerErrorPage(model);
        }
        return "review-subordinates";
    }

    @GetMapping(path = "/respond/{id}")
    public String getRespondPage(@PathVariable(value = "id") int approvalId, Model model, OAuth2AuthenticationToken authenticationToken) {
        Manager manager;
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            manager = managerService.getManagerById(employee.getId());
            Optional<Approval> approval = approvalService.getApprovalById(approvalId);
            if (!approval.isPresent()) {
                return getApprovalNotFoundErrorPage(model);
            }
            else if (approval.get().getManager().getId() != manager.getId()) {
                return getApprovalNotSuperiorOfRequesterErrorPage(model);
            } else {
                model.addAttribute("approval", approval.get());
                model.addAttribute("request", approval.get().getRequest());
                model.addAttribute("allApprovals", approval.get().getRequest().getApprovals());
                model.addAttribute("manager", approval.get().getManager());
                model.addAttribute("employee", approval.get().getManager().getEmployee());
            }
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        } catch (NotAManagerException e) {
            return getNotAManagerErrorPage(model);
        }
        return "respond";
    }


}
