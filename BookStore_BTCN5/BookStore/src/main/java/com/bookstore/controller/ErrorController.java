package com.bookstore.controller;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Controller
@RequestMapping("/error")
public class ErrorController {

    private final ErrorAttributes errorAttributes;

    public ErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public String handleError(WebRequest webRequest, Model model) {
        Map<String, Object> errors = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));
        int status = (int) errors.get("status");

        model.addAttribute("status", status);
        model.addAttribute("error", errors.get("error"));
        model.addAttribute("message", errors.get("message"));

        if (status == 400) {
            return "error/400";
        } else if (status == 403) {
            return "error/403";
        } else if (status == 404) {
            return "error/404";
        } else if (status == 500) {
            return "error/500";
        } else {
            return "error/genericError";
        }
    }
}