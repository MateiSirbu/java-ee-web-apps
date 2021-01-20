package eu.msirbu.tw.tema3.controllers;

import eu.msirbu.tw.tema3.entities.*;
import eu.msirbu.tw.tema3.exceptions.NotAManagerException;
import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static eu.msirbu.tw.tema3.controllers.utils.Utils.*;

@Controller
public class RequestController {

    /* Autowired services */

    private OAuth2AuthorizedClientService authorizedClientService;
    private EmployeeService employeeService;
    private ManagerService managerService;
    private RequestService requestService;
    private StatusService statusService;
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
    public void setManagerService(ManagerService managerService) {
        this.managerService = managerService;
    }

    @Autowired
    public void setRequestService(RequestService requestService) {
        this.requestService = requestService;
    }

    @Autowired
    public void setStatusService(StatusService statusService) {
        this.statusService = statusService;
    }

    @Autowired
    public void setPublicHolidayService(PublicHolidayService publicHolidayService) {
        this.publicHolidayService = publicHolidayService;
    }

    /**
     * Request form endpoint.
     */
    @GetMapping("/request")
    public String getRequestPage(Model model, OAuth2AuthenticationToken authenticationToken) {
        Manager employeeAsManager;
        Employee employee;
        LocalDate effectiveDate, untilDate;
        effectiveDate = LocalDate.now().plus(1, ChronoUnit.WEEKS);
        untilDate = LocalDate.now().plus(2, ChronoUnit.WEEKS);
        model.addAttribute("effectiveDate", effectiveDate);
        model.addAttribute("untilDate", untilDate);
        try {
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);
            employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            int requestableVacationDays = employee.getRequestableVacationDays(publicHolidayService);
            if (requestableVacationDays == 0)
                return getQuotaLimitReachedErrorPage(model);
            model.addAttribute("remainingDays", employee.getRemainingVacationDays(publicHolidayService));
            model.addAttribute("requestableDays", requestableVacationDays);
            model.addAttribute("approvedDays", employee.getApprovedDays(publicHolidayService));
            model.addAttribute("pendingDays", employee.getPendingDays(publicHolidayService));
            model.addAttribute("quota", employee.getVacationDayQuota());
            model.addAttribute("teamLeaders", employee.getSuperiors());
            employeeAsManager = managerService.getManagerById(employee.getId());
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        } catch (NotAManagerException e) {
            return "request";
        }
        model.addAttribute("superior", employeeAsManager.getSuperior());
        return "request";
    }

    /**
     * Displays an error page if the dates entered are not ISO 8601 compliant.
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ModelAndView handleTypeMismatchException(TypeMismatchException e) {
        return getInvalidDateFormatErrorPage();
    }

    /**
     * Request form submission endpoint.
     */
    @PostMapping("/request_submit")
    public String submitForm(Model model, @ModelAttribute(value = "effectiveDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate effective, @ModelAttribute(value = "untilDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until, OAuth2AuthenticationToken authenticationToken) {
        Request requestToSubmit = null;
        List<Approval> newApprovals = new ArrayList<>();

        // if 'effective' date is set later than 'until' date, abort
        if (effective.isAfter(until)) {
            return getInvalidDateOrderErrorPage(model);
        }

        // if 'effective' date is set earlier than five days in the future, abort
        if (effective.isBefore(LocalDate.now().plus(5, ChronoUnit.DAYS))) {
            return getInvalidDateSetTooEarlyErrorPage(model);
        }

        // if 'until' date is set later than six months in the future, abort
        if (until.isAfter(LocalDate.now().plus(6, ChronoUnit.MONTHS))) {
            return getInvalidDateSetTooLateErrorPage(model);
        }

        final long NUMBER_OF_SELECTED_VACATION_DAYS = ChronoUnit.DAYS.between(effective, until.plusDays(1)) - countExemptDays(effective, until, publicHolidayService);

        // if the range is empty or the dates inside the range are all exempt from being counted towards the quota, abort
        if (NUMBER_OF_SELECTED_VACATION_DAYS == 0) {
            return getInvalidDateEmptyRangePage(model);
        }

        // user-agnostic parameters are valid, retrieving user profile and verifying eligibility:
        try {
            final Status PENDING = statusService.getStatusByName("PENDING");
            final Status APPROVED = statusService.getStatusByName("APPROVED");

            // validate credentials
            getLoginInfo(model, authenticationToken, authorizedClientService, employeeService);

            // get employee info
            Employee employee = employeeService.getEmployeeByEmail((String) model.getAttribute("email"));
            requestToSubmit = new Request(employee, effective, until);

            // get existing pending/approved requests; if one of them overlaps with this one, abort
            List<Request> existingRequests = employee.getRequests();
            for (Request existingRequest: existingRequests) {
                final boolean isOverlapping = requestToSubmit.getStartDate().isBefore(existingRequest.getEndDate()) && existingRequest.getStartDate().isBefore(requestToSubmit.getEndDate());
                final boolean isApproved = (existingRequest.getAggregatedStatus().equals(APPROVED));
                final boolean isPending = (existingRequest.getAggregatedStatus().equals(PENDING));
                if (isOverlapping && (isApproved || isPending))
                    return getInvalidDateOverlapErrorPage(model);
            }

            // if the selected period exceeds quota, abort
            if (NUMBER_OF_SELECTED_VACATION_DAYS > employee.getRequestableVacationDays(publicHolidayService))
                return getExceededQuotaErrorPage(model);

            // if data entered is within constraints, attempt form submission:

            // get team leaders and request their approval
            List<Manager> superiors = employee.getSuperiors();
            for (Manager superior : superiors) {
                newApprovals.add(new Approval(requestToSubmit, superior, PENDING));
            }

            // get employee info as the manager (if possible, user might be a regular employee)
            Manager employeeAsManager = managerService.getManagerById(employee.getId());

            // if CEO, approve request automatically
            if (superiors.size() == 0 && employeeAsManager.getSuperior() == null) {
                newApprovals.add(new Approval(requestToSubmit, employeeAsManager, APPROVED));
                requestToSubmit.setApprovals(newApprovals);
                requestService.addRequest(requestToSubmit);
                return getRequestSuccessCEOPage(model);
            }

            // otherwise, request approval from all superiors
            newApprovals.add(new Approval(requestToSubmit, employeeAsManager.getSuperior(), PENDING));
            requestToSubmit.setApprovals(newApprovals);
            requestService.addRequest(requestToSubmit);

        // if user is in Active Directory but not in database, deny access
        } catch (NotFoundException e) {
            return getNotEnrolledErrorPage(model);
        // if user is not a manager, request approval from team leaders only
        } catch (NotAManagerException e) {
            requestToSubmit.setApprovals(newApprovals);
            requestService.addRequest(requestToSubmit);
        }
        // if all goes well, show a smiley face :)
        return getRequestSuccessPage(model);
    }
}