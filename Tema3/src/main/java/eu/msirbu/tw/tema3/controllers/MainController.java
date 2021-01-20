package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.Approval;
import eu.msirbu.tw.tema3.entities.Employee;
import eu.msirbu.tw.tema3.entities.Manager;
import eu.msirbu.tw.tema3.entities.PublicHoliday;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.ApprovalService;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.ManagerService;
import eu.msirbu.tw.tema3.services.PublicHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

import static eu.msirbu.tw.tema3.controllers.utils.Utils.*;

@Controller
public class MainController {

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

    @GetMapping("/")
    public String getLoginPage() {
        return "index";
    }

    @GetMapping("/public-holidays")
    public String getPublicHolidaysPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        List<PublicHoliday> publicHolidayList;
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            publicHolidayList = publicHolidayService.getAllPublicHolidays();
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        }
        model.addAttribute("holidays", publicHolidayList);
        return "public-holidays";
    }

    @GetMapping("/dashboard")
    public String getManagePage(Model model, OAuth2AuthenticationToken authenticationToken) {
        Manager manager;
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            System.out.println(employee.getTeams());
            model.addAttribute("remainingDays", employee.getRemainingVacationDays(publicHolidayService));
            model.addAttribute("requestableDays", employee.getRequestableVacationDays(publicHolidayService));
            model.addAttribute("approvedDays", employee.getApprovedDays(publicHolidayService));
            model.addAttribute("pendingDays", employee.getPendingDays(publicHolidayService));
            model.addAttribute("quota", employee.getVacationDayQuota());
            manager = managerService.getManagerById(employee.getId());
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        } catch (NotAManagerException e) {
            manager = null;
        }
        model.addAttribute("manager", manager);
        return "dashboard";
    }
}
