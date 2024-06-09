package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.dto.registerForm.RegistrationDTO;
import fr.uha.AccountingFlowManager.enums.Country;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private final UserService userService;

    @Autowired
    public AuthenticationController(UserService userService) {
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

    @GetMapping({"/register"})
    public String renderRegister(Model model) {

        model.addAttribute("registerForm", true);
        model.addAttribute("providers", userService.getAllProviders());
        model.addAttribute("countries", Country.values());
        System.out.println("############################all providers");
        System.out.println(userService.getAllProviders());
        return "/authentication/registerForm";
    }

    @PostMapping({"/registerAction"})
    public String register(@ModelAttribute("registerFormData") RegistrationDTO registrationDTO, RedirectAttributes redirectAttributes) {
        try {
            System.out.println(registrationDTO);
            userService.registerNewUser(registrationDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Inscription réussie. Veuillez vous connecter.");
            return "redirect:/login";
        } catch (Exception e) {
            String errorMessage = "Une erreur s'est produite lors de l'inscription. Veuillez réessayer plus tard.";
            if (e instanceof IllegalArgumentException && e.getMessage().equals("Email already in use")) {
                errorMessage = "Cet email est déjà utilisé. Veuillez vous connecter.";
            }
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            redirectAttributes.addFlashAttribute("registerFormData", registrationDTO);
            return "redirect:/register";
        }
    }

}