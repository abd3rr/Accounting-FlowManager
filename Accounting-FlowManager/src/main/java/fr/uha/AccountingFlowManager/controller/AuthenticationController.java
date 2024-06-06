package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService){
        this.userService = userService;
    }
    @GetMapping({"/login"})
    public String index(Model model) {
        model.addAttribute("loginForm", true);
        return "/authentication/loginForm";
    }

    @GetMapping({"/logout"})
    public String logout(Model model) {
        model.addAttribute("logoutForm", true);
        model.addAttribute("userId", userService.getCurrentUserId());

        return "/authentication/logoutForm";
    }

}
