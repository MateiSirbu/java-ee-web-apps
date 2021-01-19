package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.entities.Request;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
import eu.msirbu.tw.tema3.services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static eu.msirbu.tw.tema3.controllers.utils.ControllerUtils.*;

@Controller
public class RequestController {

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;
    private RequestService requestService;

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
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/request")
    public String getRequestPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        Manager manager;
        Employee employee;
        LocalDate effectiveDate, untilDate;
        effectiveDate = LocalDate.now().plus(1, ChronoUnit.WEEKS);
        untilDate = LocalDate.now().plus(2, ChronoUnit.WEEKS);
        model.addAttribute("effectiveDate", effectiveDate);
        model.addAttribute("untilDate", untilDate);
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            model.addAttribute("teamLeaders", employee.getSuperiors());
            manager = managerService.getManagerById(employee.getId());
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        } catch (NotAManagerException e) {
            return "request";
        }
        model.addAttribute("superior", manager.getSuperior());
        return "request";
    }

    @PostMapping("/request_submit")
    public String submitForm(Model model, @ModelAttribute(value="effectiveDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effective, @ModelAttribute(value="untilDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until, OAuth2AuthenticationToken authenticationToken) {
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            Request request = new Request(employee, effective, until);
            List<Manager> superiors = employee.getSuperiors();
            for (Manager superior: superiors) {
                request.getApprovals().add(new Approval(superior, ))
            }
            requestService.addRequest(request);
        }
        catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        }
        return getRequestSuccessPage(model);
    }
}