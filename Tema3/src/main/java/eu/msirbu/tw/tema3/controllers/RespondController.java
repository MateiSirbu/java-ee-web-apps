package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static eu.msirbu.tw.tema3.controllers.utils.ControllerUtils.*;

@Controller
public class RespondController {

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;

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

    @GetMapping("/respond")
    public String getRespondPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        Manager manager = null;
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            manager = managerService.getManagerById(employee.getId());
        } catch (NotFoundException e) {
            return getUserNotEnrolledPage(model);
        }
        catch (NotAManagerException e) {
            return getUserNotAManagerPage(model);
        }
        return "respond";
    }
}
