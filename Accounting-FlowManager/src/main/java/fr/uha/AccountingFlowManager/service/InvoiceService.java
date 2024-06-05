package fr.uha.AccountingFlowManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceDisplayDTO;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceItemDTO;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceLineDisplayDTO;
import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import fr.uha.AccountingFlowManager.enums.*;
import fr.uha.AccountingFlowManager.model.File;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.FileRepository;
import fr.uha.AccountingFlowManager.repository.InvoiceRepository;
import fr.uha.AccountingFlowManager.repository.ProductRepository;
import fr.uha.AccountingFlowManager.repository.UserRepository;
import fr.uha.AccountingFlowManager.util.InvoiceDtoHelper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final AIExtractionService aiExtractionService;
    private final FileRepository fileRepository;
    private final ProductRepository productRepository;
    private final RoleService roleService;
    private final UserRepository userRepository;
    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, UserService userService,
                          ProductService productService, InvoiceLineService invoiceLineService,
                          TransactionService transactionService, AIExtractionService aiExtractionService, ObjectMapper objectMapper, FileRepository fileRepository, ProductRepository productRepository, RoleService roleService, UserRepository userRepository) {
        this.invoiceRepository = invoiceRepository;
        this.userService = userService;
        this.productService = productService;
        this.invoiceLineService = invoiceLineService;
        this.transactionService = transactionService;
        this.aiExtractionService = aiExtractionService;
        this.fileRepository = fileRepository;
        this.productRepository = productRepository;
        this.roleService = roleService;
        this.userRepository = userRepository;
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


        transactionService.createTransaction(previewDTO.getTotalTTC(), provider, invoice, TransactionType.DEBIT);


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

    @Transactional
    public void saveInvoiceFromFile(File savedFile) {
        PDDocument document = null;
        PDFTextStripper pdfStripper = null;
        String text = null;
        try {
            java.io.File file = new java.io.File(savedFile.getFilePath());
            document = PDDocument.load(file);
            pdfStripper = new PDFTextStripper();
            text = pdfStripper.getText(document);

            String invoiceData = aiExtractionService.getInvoiceDataResponse(text);
            System.out.println("Invoice data: " + invoiceData);
            User provider = userService.getUserById(userService.getCurrentUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Provider not found"));

            System.out.println("Invoice Display upp" + InvoiceDtoHelper.uploadedInvoiceToDisplayDTO(invoiceData, provider));
            saveInvoice(InvoiceDtoHelper.uploadedInvoiceToDisplayDTO(invoiceData, provider), savedFile);
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Invoice saveInvoice(InvoiceDisplayDTO invoiceDisplayDTO, File file) {
        User provider = userService.getUserById(userService.getCurrentUserId())
                .orElseThrow(() -> new IllegalArgumentException("Provider not found"));
        User client = userService.getUserByEmail(invoiceDisplayDTO.getCustomerEmail());
        if (client == null) {
            client = userService.getUserByFullName(invoiceDisplayDTO.getCustomerName());
        }
        if(client == null){
            client = new User();
            client.setFullName(invoiceDisplayDTO.getCustomerName());
            client.setRole(roleService.getOrCreateRole(RoleName.ROLE_CLIENT));
            client.setAddress(invoiceDisplayDTO.getCustomerAddress());
            client.setCountry(Country.fromString(invoiceDisplayDTO.getCustomerCountry()));
            if(invoiceDisplayDTO.getCustomerEmail()!=null) client.setEmail(invoiceDisplayDTO.getCustomerEmail());
            client = userRepository.save(client);
        }

        Invoice invoice = new Invoice();
        invoice.setCustomer(client);
        invoice.setIssueDate(invoiceDisplayDTO.getIssueDate());
        invoice.setCurrency(invoiceDisplayDTO.getCurrency());
        invoice.setSubtotal(invoiceDisplayDTO.getSubtotal());
        invoice.setDiscount(invoiceDisplayDTO.getDiscount());
        invoice.setAdvancePayment(invoiceDisplayDTO.getAdvancePayment());
        invoice.setTotal(invoiceDisplayDTO.getTotal());
        invoice.setShippingCost(invoiceDisplayDTO.getShippingCost());
        //invoice.setVat(invoiceDisplayDTO.getVat());
        invoice = invoiceRepository.save(invoice);

        for (InvoiceLineDisplayDTO lineDTO : invoiceDisplayDTO.getLines()) {
            ProductCatalog product = productService.getProductByNameAndProviderId(lineDTO.getProductName(), provider.getId()).orElse(null);
            if (product == null) {
                product = new ProductCatalog();
                product.setName(lineDTO.getProductName());
                product.setPrice(lineDTO.getUnitPrice());
                product.setCurrency(Currency.EUR);
                product.setProvider(provider);
                product = productRepository.save(product);
            }
            invoiceLineService.createInvoiceLine(invoice, product, (int) lineDTO.getQuantity(), lineDTO.getUnitPrice());
        }

        transactionService.createTransaction(invoiceDisplayDTO.getTotal(), provider, invoice, TransactionType.DEBIT);
        file.setInvoice(invoice);
        fileRepository.save(file);
        return invoice;
    }


}
