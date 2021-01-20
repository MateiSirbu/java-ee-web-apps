package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Request;
import eu.msirbu.tw.tema3.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getLoginInfo;
import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getNotEnrolledErrorPage;

@Controller
public class ReviewController {

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;

    @Autowired
    public void setAuthorizedClientService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/review")
    public String getReviewPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            List<Request> requests = employee.get().getRequests();
            requests.sort(Collections.reverseOrder());
            model.addAttribute("requests", requests);
            return "review";
        }
        return getNotEnrolledErrorPage(model);
    }
}
