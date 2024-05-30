package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.enums.TransactionType;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserService userService;
    private final ProductService productService;
    private final InvoiceLineService invoiceLineService;
    private final TransactionService transactionService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, UserService userService,
                          ProductService productService, InvoiceLineService invoiceLineService,
                          TransactionService transactionService) {
        this.invoiceRepository = invoiceRepository;
        this.userService = userService;
        this.productService = productService;
        this.invoiceLineService = invoiceLineService;
        this.transactionService = transactionService;
    }

    private static Invoice getInvoice(PreviewDTO previewDTO, User client) {
        Invoice invoice = new Invoice();
        invoice.setCustomer(client);
        invoice.setCurrency(Currency.EUR);
        invoice.setSubtotal(previewDTO.getTotalHT());
        invoice.setDiscount(previewDTO.getTotalReduction());
        invoice.setAdvancePayment(previewDTO.getAdvancePayment());
        invoice.setTotal(previewDTO.getTotalTTC());
        invoice.setShippingCost(previewDTO.getShippingCost());
        invoice.setShippingCostType(previewDTO.getShippingCostType());
        invoice.setVat(previewDTO.getTva());
        return invoice;
    }

    @Transactional
    public Invoice createInvoiceAndTransaction(PreviewDTO previewDTO) {

        User provider = userService.getUserById(userService.getCurrentUserId()).
                orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        User client = userService.getUserById(Long.valueOf(previewDTO.getClientId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Invoice invoice = getInvoice(previewDTO, client);

        invoice = invoiceRepository.save(invoice);

        for (PreviewDTO.PreviewProduct productDto : previewDTO.getProducts()) {
            ProductCatalog product = productService.getProductById(Long.valueOf(productDto.getProductId()));
            invoiceLineService.createInvoiceLine(invoice, product, productDto.getQuantity(), productDto.getUnitPrice());
        }

        if (previewDTO.getAdvancePayment() > 0) {
            transactionService.createTransaction(previewDTO.getAdvancePayment(), provider, invoice, TransactionType.DEBIT);
        }

        return invoice;
    }
}
