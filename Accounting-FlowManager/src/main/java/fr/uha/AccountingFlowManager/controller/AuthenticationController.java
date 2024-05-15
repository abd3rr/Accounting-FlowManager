package fr.uha.AccountingFlowManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    @GetMapping({"/login"})
    public String index(Model model) {
        model.addAttribute("loginForm", true);
        return "/authentication/loginForm";
    }
}
