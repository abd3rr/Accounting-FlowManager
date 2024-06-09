package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.dto.AccountTransactionsDTO;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class PageController {

    private final UserService userService;

    @Autowired
    public PageController(UserService userService) {
        this.userService = userService;
    }

    private void setActivePage(Model model, String page) {
        model.addAttribute(page, true);
    }

    @GetMapping({"/index", "/"})
    public String renderIndex(Model model) {
        model.addAttribute("index", true);

        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        setActivePage(model, "home");
        model.addAttribute("home", true);
        Long userId = userService.getCurrentUserId();
        User currentUser = userService.getUserById(userId).orElse(null);
        if (currentUser != null && userService.getCurrentUserRole().equals(RoleName.ROLE_PROVIDER.toString())) {
            AccountTransactionsDTO accountTransactionsDTO = userService.getAccountTransactionsDTOByUser(currentUser);
            model.addAttribute("accountTransactions", accountTransactionsDTO);
            System.out.println(accountTransactionsDTO);

        }
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
        setActivePage(model, "terms");
        return "terms";
    }

    @GetMapping("/services")
    public String services(Model model) {
        setActivePage(model, "services");
        return "services";
    }

    @GetMapping("/json/French.json")
    public ResponseEntity<Resource> getFrenchJson() {
        Resource file = new ClassPathResource("static/json/French.json");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .body(file);
    }

}