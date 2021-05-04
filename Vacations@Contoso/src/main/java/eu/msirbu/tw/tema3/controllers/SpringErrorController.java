/*
 * Vacations @ Contoso
 * (C) 2021 Matei Sîrbu.
 */
package eu.msirbu.tw.tema3.controllers;

import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.WebRequest;

/**
 * Handles Spring Boot errors.
 */
@Controller
public class SpringErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public SpringErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @GetMapping("/error")
    public String handleError(Model model, WebRequest webRequest) {
        final Throwable error = errorAttributes.getError(webRequest);
        model.addAttribute("errorHeadline", "Spring Boot has panicked.");
        model.addAttribute("errorDetails", error == null ? "No error message available." : error.getMessage());
        model.addAttribute("errorAnchorHref", "/");
        model.addAttribute("errorAnchorText", "BACK TO SAFETY");
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
