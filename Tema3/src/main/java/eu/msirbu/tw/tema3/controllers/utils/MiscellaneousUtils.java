package eu.msirbu.tw.tema3.controllers.utils;

import eu.msirbu.tw.tema3.services.EmployeeService;
import eu.msirbu.tw.tema3.services.PublicHolidayService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class MiscellaneousUtils {

    public static void getLoginInfo(Model model, OAuth2AuthenticationToken authenticationToken, OAuth2AuthorizedClientService authorizedClientService, EmployeeService employeeService) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(authenticationToken.getAuthorizedClientRegistrationId(), authenticationToken.getName());
        String userInfoEndpointUri = client.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        if (!StringUtils.isEmpty(userInfoEndpointUri)) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + client.getAccessToken().getTokenValue());
            HttpEntity<String> entity = new HttpEntity<>("", headers);
            ResponseEntity<Map> response = restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, Map.class);
            Map userAttributes = response.getBody();
            model.addAttribute("name", userAttributes.get("name"));
            model.addAttribute("email", userAttributes.get("email"));
            employeeService.getEmployeeByEmail((String) userAttributes.get("email"));
        }
    }
    public static int countExemptDays(LocalDate start, LocalDate end, PublicHolidayService publicHolidayService) {
        long numberOfDays = ChronoUnit.DAYS.between(start, end.plusDays(1));
        int exemptDays = 0;
        for (int i = 0; i < numberOfDays; i++) {
            LocalDate day = start.plusDays(i);
            DayOfWeek dayOfWeek = day.getDayOfWeek();
            if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY || publicHolidayService.getPublicHolidayByDate(day).isPresent()) {
                exemptDays++;
            }
        }
        return exemptDays;
    }
    public static String getNotEnrolledErrorPage(Model model) {
        model.addAttribute("errorHeadline", "You are not authorized to view this resource.");
        model.addAttribute("errorDetails", "You are signed in via Contoso's Azure AD, but this user is not enrolled in Vacations@Contoso.");
        model.addAttribute("errorAnchorHref", "/");
        model.addAttribute("errorAnchorText", "BACK TO THE HOMEPAGE");
        return "error";
    }
    public static String getNotAManagerErrorPage(Model model) {
        model.addAttribute("errorHeadline", "You are not authorized to view this resource.");
        model.addAttribute("errorDetails", "You are signed in via Contoso's Azure AD, but you cannot respond to requests since you are not a manager.");
        model.addAttribute("errorAnchorHref", "/dashboard");
        model.addAttribute("errorAnchorText", "BACK TO THE DASHBOARD");
        return "error";
    }
    public static ModelAndView getInvalidDateFormatErrorPage() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("errorHeadline", "Invalid date format.");
        mav.addObject("errorDetails", "Your request cannot be sent because the dates you entered are invalid.\n" +
                "Make sure the dates are represented according to the ISO 8601 standard (e.g. 2020-12-31).");
        mav.addObject("errorAnchorHref", "/request");
        mav.addObject("errorAnchorText", "BACK TO THE REQUEST FORM");
        mav.setViewName("error");
        return mav;
    }
    public static String getInvalidDateOrderErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Invalid dates.");
        model.addAttribute("errorDetails", "The 'effective' date should be set earlier than the 'until' date.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getInvalidDateSetTooEarlyErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Invalid dates.");
        model.addAttribute("errorDetails", "The effective date should be set no earlier than five days in the future.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getInvalidDateSetTooLateErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Invalid dates.");
        model.addAttribute("errorDetails", "The 'until' date should be set no later than six months in the future.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getInvalidDateOverlapErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Invalid dates.");
        model.addAttribute("errorDetails", "The dates you entered overlap already accepted and/or pending vacation requests.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getInvalidDateEmptyRangePage(Model model) {
        model.addAttribute("errorHeadline", "Invalid dates.");
        model.addAttribute("errorDetails", "The range you selected does not include any vacation dates.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getQuotaLimitReachedErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Quota limit reached.");
        model.addAttribute("errorDetails", "You can no longer submit requests because your vacation quota limit has been reached.");
        model.addAttribute("errorAnchorHref", "/dashboard");
        model.addAttribute("errorAnchorText", "BACK TO THE DASHBOARD");
        return "error";
    }
    public static String getExceededQuotaErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Quota exceeded.");
        model.addAttribute("errorDetails", "The request cannot be sent because the days you entered exceed the vacation day quota.");
        model.addAttribute("errorAnchorHref", "/request");
        model.addAttribute("errorAnchorText", "BACK TO THE REQUEST FORM");
        return "error";
    }
    public static String getApprovalNotSuperiorOfRequesterErrorPage(Model model) {
        model.addAttribute("errorHeadline", "You are not authorized to view this resource.");
        model.addAttribute("errorDetails", "You cannot respond to this review because you do not manage the requester.");
        model.addAttribute("errorAnchorHref", "/review-subordinates");
        model.addAttribute("errorAnchorText", "BACK TO SUBORDINATES' REQUESTS");
        return "error";
    }
    public static String getApprovalNotFoundErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Request not found.");
        model.addAttribute("errorDetails", "This is not the request you are looking for.");
        model.addAttribute("errorAnchorHref", "/review-subordinates");
        model.addAttribute("errorAnchorText", "BACK TO SUBORDINATES' REQUESTS");
        return "error";
    }
    public static String getResponseSentDuplicateErrorPage(Model model) {
        model.addAttribute("errorHeadline", "Cannot submit response.");
        model.addAttribute("errorDetails", "You have already submitted a response to this request.");
        model.addAttribute("errorAnchorHref", "/review-subordinates");
        model.addAttribute("errorAnchorText", "BACK TO SUBORDINATES' REQUESTS");
        return "error";
    }
    public static String getRequestSuccessPage(Model model) {
        model.addAttribute("successHeadline", "Request sent.");
        model.addAttribute("successDetails", "You request has been submitted successfully.");
        model.addAttribute("successAnchorHref", "/dashboard");
        model.addAttribute("successAnchorText", "BACK TO THE DASHBOARD");
        return "success";
    }
    public static String getRequestSuccessCEOPage(Model model) {
        model.addAttribute("successHeadline", "Request accepted.");
        model.addAttribute("successDetails", "Your request has been submitted and accepted.");
        model.addAttribute("successAnchorHref", "/dashboard");
        model.addAttribute("successAnchorText", "BACK TO THE DASHBOARD");
        return "success";
    }
    public static String getResponseSentSuccessPage(Model model) {
        model.addAttribute("successHeadline", "Response sent.");
        model.addAttribute("successDetails", "Your response has been submitted successfully.");
        model.addAttribute("successAnchorHref", "/review-subordinates");
        model.addAttribute("successAnchorText", "BACK TO SUBORDINATES' REQUESTS");
        return "success";
    }
}
