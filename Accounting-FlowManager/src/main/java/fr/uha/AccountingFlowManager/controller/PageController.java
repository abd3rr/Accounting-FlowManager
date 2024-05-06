package fr.uha.AccountingFlowManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/","/index"})
    public String index(Model model) {
        return "home";
    }
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }
    @GetMapping("/help")
    public String help(Model model) {
        return "help";
    }
    @GetMapping("/terms")
    public String terms(Model model) {
        return "terms";
    }
    @GetMapping("/services")
    public String services(Model model) {
        return "services";
    }
}
