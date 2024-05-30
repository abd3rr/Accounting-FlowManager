package fr.uha.AccountingFlowManager.controller;


import fr.uha.AccountingFlowManager.dto.invoice.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import fr.uha.AccountingFlowManager.enums.ReductionType;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.service.InvoiceService;
import fr.uha.AccountingFlowManager.service.ProductService;
import fr.uha.AccountingFlowManager.service.UserService;
import fr.uha.AccountingFlowManager.util.InvoiceDtoHelper;
import jakarta.servlet.http.HttpSession;
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
    private final ProductService productService;
    private final InvoiceService invoiceService;
    @Autowired
    public InvoiceController(UserService userService, ProductService productService, InvoiceService invoiceService) {
        this.userService = userService;
        this.productService = productService;
        this.invoiceService = invoiceService;
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
    public String renderPreviewInvoice(@ModelAttribute("invoiceFormData") InvoiceFormDataDTO invoiceFormDataDTO, Model model, HttpSession session) {
        // Filter out invalid products
        List<InvoiceFormDataDTO.ProductInvoiceForm> validProducts = invoiceFormDataDTO.getProducts().stream()
                .filter(product -> product.getProductId() != null && !product.getProductId().isEmpty())
                .collect(Collectors.toList());
        invoiceFormDataDTO.setProducts(validProducts);

        // Retrieve client and provider information
        User client = userService.getUserById(Long.valueOf(invoiceFormDataDTO.getClientId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + invoiceFormDataDTO.getClientId()));
        User provider = userService.getUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found: " + userService.getCurrentUserId()));

        // Retrieve product information
        List<ProductCatalog> products = validProducts.stream()
                .map(productInvoiceForm -> productService.getProductById(Long.valueOf(productInvoiceForm.getProductId())))
                .collect(Collectors.toList());

        // Create preview DTO
        PreviewDTO previewDTO = InvoiceDtoHelper.createPreviewDTO(client, provider, products, invoiceFormDataDTO);

        // Add preview DTO to the model
        model.addAttribute("invoicePreview", true);
        model.addAttribute("previewDTO", previewDTO);
        session.setAttribute("previewDTO", previewDTO);


        return "/invoice/invoicePreview";
    }

    @GetMapping("/invoice/create")
    public String createInvoice(HttpSession session, Model model) {
        PreviewDTO previewDTO = (PreviewDTO) session.getAttribute("previewDTO");
        if (previewDTO == null) {
           System.out.println("error create null previewDTO");
        }
        System.out.println(previewDTO);

        if (previewDTO != null) {
            invoiceService.createInvoiceAndTransaction(previewDTO);
        }
        session.removeAttribute("previewDTO");

        return "redirect:/invoice/list";
    }

    @GetMapping("/invoice/list")
    public String listInvoices(Model model) {
        return "/invoice/invoiceList";
    }


}
