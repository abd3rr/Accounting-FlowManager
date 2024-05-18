package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

   private final UserService userService;

    @Autowired
    public PageController(UserService userService){
        this.userService = userService;
    }

    private void setActivePage(Model model, String page) {
        model.addAttribute(page, true);
    }

    @GetMapping({"/","/home"})
    public String index(Model model) {
        setActivePage(model, "home");
        model.addAttribute("userRole", userService.getCurrentUserRole());
        model.addAttribute("userId",userService.getCurrentUserId());
        model.addAttribute("userEmail",userService.getCurrentUserEmail());

        return "home";
    }

    @GetMapping("/about")
    public String about(Model model) {
        setActivePage(model, "about");
        return "about";
    }
    @GetMapping("/help")
    public String help(Model model) {
        setActivePage(model, "help");
        return "help";
    }
    @GetMapping("/terms")
    public String terms(Model model) {
        setActivePage(model,  "terms");
        return "terms";
    }
    @GetMapping("/services")
    public String services(Model model) {
        setActivePage(model,  "services");
        return "services";
    }
}