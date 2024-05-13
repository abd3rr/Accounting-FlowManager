package fr.uha.AccountingFlowManager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InvoiceController {
    @GetMapping("/addInvoice")
    private String renderInvoiceForm(Model model){
        model.addAttribute("invoiceAddForm", true);
        return "/invoice/invoiceAddForm";
    }

}
