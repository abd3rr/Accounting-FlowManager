package fr.uha.AccountingFlowManager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    private void setActivePage(Model model, String page) {
        model.addAttribute(page, true);
    }

    @GetMapping({"/home"})
    public String index(Model model) {
        setActivePage(model, "home");
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