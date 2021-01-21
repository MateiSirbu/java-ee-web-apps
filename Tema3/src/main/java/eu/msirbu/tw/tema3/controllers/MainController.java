/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
import eu.msirbu.tw.tema3.services.PublicHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getLoginInfo;
import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getNotEnrolledErrorPage;

@Controller
public class MainController {

    /* Autowired services */

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;
    private PublicHolidayService publicHolidayService;

    @Autowired
    public void setAuthorizedClientService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Autowired
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Autowired
    public void setPublicHolidayService(PublicHolidayService publicHolidayService) {
        this.publicHolidayService = publicHolidayService;
    }

    @Autowired
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    /* Mappings */

    /**
     * Home page endpoint.
     */
    @GetMapping("/")
    public String getLoginPage() {
        return "index";
    }

    /**
     * Dashboard endpoint.
     */
    @GetMapping("/dashboard")
    public String getManagePage(Model model, OAuth2AuthenticationToken authenticationToken) {
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        Optional<Employee> employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
        if (employee.isPresent()) {
            model.addAttribute("remainingDays", employee.get().getRemainingVacationDays(publicHolidayService));
            model.addAttribute("requestableDays", employee.get().getRequestableVacationDays(publicHolidayService));
            model.addAttribute("approvedDays", employee.get().getApprovedDays(publicHolidayService));
            model.addAttribute("pendingDays", employee.get().getPendingDays(publicHolidayService));
            model.addAttribute("quota", employee.get().getVacationDayQuota());
            Optional<Manager> manager = managerService.getManagerById(employee.get().getId());
            if (manager.isPresent()) {
                model.addAttribute("manager", manager);
            }
            return "dashboard";
        }
        return getNotEnrolledErrorPage(model);
    }
}
