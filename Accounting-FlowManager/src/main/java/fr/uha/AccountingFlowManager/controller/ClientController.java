package fr.uha.AccountingFlowManager.controller;

import fr.uha.AccountingFlowManager.dto.client.ProvidersAddDTO;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientController {

    private final UserService userService;

    @Autowired
    public ClientController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/client/providerAdd"})
    public String renderAddProviderForm(Model model) {
        System.out.println(userService.getAllProvidersExcludingClients(userService.getCurrentUserId()));
        model.addAttribute("addProviderForm", true);
        model.addAttribute("providers", userService.getAllProvidersExcludingClients(userService.getCurrentUserId()));
        return "/client/addProviderForm";
    }

    @PostMapping({"/client/providerAddAction"})
    public String addProviderAction(@ModelAttribute("clientAddProvidersFormData") ProvidersAddDTO providersAddDTO, Model model) {
        System.out.println("Entered addProviderAction" + providersAddDTO);

        Long currentUserId = userService.getCurrentUserId();
        System.out.println("Current user ID: " + currentUserId);

        try {
            System.out.println("Attempting to add client providers");
            userService.addClientProviders(currentUserId, providersAddDTO.getProviders());
            System.out.println("Providers added successfully");
        } catch (Exception e) {
            System.out.println("Error adding providers: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Redirecting to /client/listProviders");
        return "redirect:/client/listProviders";
    }

    @GetMapping({"client/listProviders"})
    public String renderListProviders(Model model) {
        System.out.println("Rendering list of providers");
        System.out.println(userService.getClientProviders(userService.getCurrentUserId()));
        model.addAttribute("providers", userService.getClientProviders(userService.getCurrentUserId()));
        return "/client/listProvider";
    }

    @GetMapping("/client/deleteProvider/{providerId}")
    public String deleteProvider(@PathVariable Long providerId) {
        Long currentUserId = userService.getCurrentUserId();
        if (currentUserId != null) {
            userService.deleteProviderForClient(currentUserId, providerId);
        }
        return "redirect:/client/listProviders";
    }
}

