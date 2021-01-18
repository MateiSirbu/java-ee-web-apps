package eu.msirbu.tw.tema3.controllers.utils;

import eu.msirbu.tw.tema3.exceptions.NotFoundException;
import eu.msirbu.tw.tema3.services.EmployeeService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.util.Map;

public class ControllerUtils {
    public static void getLoginInfo(Model model, OAuth2AuthenticationToken authenticationToken, OAuth2AuthorizedClientService authorizedClientService, EmployeeService employeeService) throws NotFoundException {
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
    public static String getUserNotEnrolledPage(Model model) {
        model.addAttribute("errorHeadline", "You are not authorized to view this resource.");
        model.addAttribute("errorDetails", "You are signed in via Contoso's Azure AD, but this user is not enrolled in Vacations@Contoso.");
        model.addAttribute("errorAnchorHref", "/");
        model.addAttribute("errorAnchorText", "BACK TO THE HOMEPAGE");
        return "error";
    }
    public static String getUserNotAManagerPage(Model model) {
        model.addAttribute("errorHeadline", "You are not authorized to view this resource.");
        model.addAttribute("errorDetails", "You are signed in via Contoso's Azure AD, but you cannot respond to requests since you are not a manager.");
        model.addAttribute("errorAnchorHref", "/dashboard");
        model.addAttribute("errorAnchorText", "BACK TO THE DASHBOARD");
        return "error";
    }
    public static String getRequestSuccessPage(Model model) {
        model.addAttribute("successHeadline", "Request sent.");
        model.addAttribute("successDetails", "You request has been submitted successfully.");
        model.addAttribute("successAnchorHref", "/dashboard");
        model.addAttribute("successAnchorText", "BACK TO THE DASHBOARD");
        return "success";
    }
}
