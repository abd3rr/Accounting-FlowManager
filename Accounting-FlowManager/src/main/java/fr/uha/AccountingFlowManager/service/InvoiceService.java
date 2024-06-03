package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.invoice.InvoiceItemDTO;
import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import fr.uha.AccountingFlowManager.enums.TransactionType;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.InvoiceRepository;
import fr.uha.AccountingFlowManager.util.InvoiceDtoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceDisplayDTO;

import java.util.List;
import java.util.stream.Collectors;

import static fr.uha.AccountingFlowManager.util.InvoiceDtoHelper.previewDtoToInvoice;

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


    @Transactional
    public Invoice createInvoiceAndTransaction(PreviewDTO previewDTO) {

        User provider = userService.getUserById(userService.getCurrentUserId()).
                orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        User client = userService.getUserById(Long.valueOf(previewDTO.getClientId()))
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Invoice invoice = previewDtoToInvoice(previewDTO, client);

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

    @Transactional(readOnly = true)
    public List<InvoiceItemDTO> getAllInvoiceItemDTOs() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(InvoiceDtoHelper::mapToInvoiceItemDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceDisplayDTO getInvoiceDisplayDTO(long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

        User provider = userService.getUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

        return InvoiceDtoHelper.invoiceToInvoiceDisplayDto(invoice, provider);
    }
}
