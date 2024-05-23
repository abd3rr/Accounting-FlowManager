package fr.uha.AccountingFlowManager.controller;


import fr.uha.AccountingFlowManager.dto.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class InvoiceController {

    private final UserService userService;

    @Autowired
    public InvoiceController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/addInvoice")
    private String renderInvoiceForm(Model model){
        model.addAttribute("invoiceAddForm", true);
        model.addAttribute("clients",userService.getCurrentProviderClients());
        System.out.println(userService.getCurrentProviderProducts());
        model.addAttribute("products",userService.getCurrentProviderProducts());
        return "/invoice/invoiceAddForm";
    }

    @PostMapping("/invoice/preview")
    public String renderPreviewInvoice(@ModelAttribute("invoiceFormData") InvoiceFormDataDTO invoiceFormDataDTO, Model model) {
        model.addAttribute("invoiceFormData", invoiceFormDataDTO);
        System.out.println(invoiceFormDataDTO);
        return "/invoice/invoicePreview";
    }

}
