package fr.uha.AccountingFlowManager.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {

        model.addAttribute("genericError", true);
        model.addAttribute("error", "An error occurred: " + e.getMessage());
        return "/error/genericError";
    }
}
