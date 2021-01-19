package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.entities.Request;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static eu.msirbu.tw.tema3.controllers.utils.Utils.getLoginInfo;
import static eu.msirbu.tw.tema3.controllers.utils.Utils.getNotEnrolledErrorPage;

@Controller
public class ReviewController {

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

    @GetMapping("/review")
    public String getReviewPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        Employee employee;
        List<Request> requests = null;
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            requests = employee.getRequests();
            requests.sort(Collections.reverseOrder());
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        } finally {
            model.addAttribute("requests", requests);
        }
        return "review";
    }
}
