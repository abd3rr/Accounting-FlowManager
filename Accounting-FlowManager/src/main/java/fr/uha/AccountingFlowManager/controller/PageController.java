package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

   /* private SessionService sessionService;

    @Autowired
    public PageController(SessionService sessionService){
        this.sessionService = sessionService;
    }
*/
    private void setActivePage(Model model, String page) {
        model.addAttribute(page, true);
    }

    @GetMapping({"/","/home"})
    public String index(Model model) {
        setActivePage(model, "home");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Extract role information
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();
        model.addAttribute("userRole", userRole);

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