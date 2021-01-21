/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.PublicHoliday;
import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.PublicHolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getLoginInfo;
import static eu.msirbu.tw.tema3.controllers.utils.MiscellaneousUtils.getNotEnrolledErrorPage;

@Controller
public class PublicHolidayController {

    /* Autowired services */

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
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

    /* Mappings */

    @GetMapping("/public-holidays")
    public String getPublicHolidaysPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
        if (!employeeService.getEmployeeByEmail((String) model.getAttribute("email")).isPresent())
            return getNotEnrolledErrorPage(model);
        List<PublicHoliday> publicHolidayList = publicHolidayService.getAllPublicHolidays();
        model.addAttribute("holidays", publicHolidayList);
        return "public-holidays";
    }
}