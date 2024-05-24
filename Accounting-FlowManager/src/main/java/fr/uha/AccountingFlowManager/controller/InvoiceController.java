package fr.uha.AccountingFlowManager.controller;


import fr.uha.AccountingFlowManager.dto.invoice.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.enums.ReductionType;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;


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
        model.addAttribute("products",userService.getCurrentProviderProducts());
        model.addAttribute("userId",userService.getCurrentUserId());
        model.addAttribute("shippingCostTypes", ShippingCostType.values());
        model.addAttribute("reductionTypes", ReductionType.values());
        return "/invoice/invoiceAddForm";
    }

    @PostMapping("/invoice/preview")
    public String renderPreviewInvoice(@ModelAttribute("invoiceFormData") InvoiceFormDataDTO invoiceFormDataDTO, Model model) {
        // Filter out invalid products
        List<InvoiceFormDataDTO.ProductInvoiceForm> validProducts = invoiceFormDataDTO.getProducts().stream()
                .filter(product -> product.getProductId() != null && !product.getProductId().isEmpty())
                .collect(Collectors.toList());
        invoiceFormDataDTO.setProducts(validProducts);

        model.addAttribute("invoiceFormData", invoiceFormDataDTO);
        System.out.println(invoiceFormDataDTO);
        return "/invoice/invoicePreview";
    }


}
