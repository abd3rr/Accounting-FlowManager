package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.invoice.*;
import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.InvoiceLine;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceDtoHelper {

    private static final double VAT_RATE = 0.15;

    public static PreviewDTO createPreviewDTO(User client, User provider, List<ProductCatalog> products, InvoiceFormDataDTO invoiceFormData) {
        PreviewDTO previewDTO = new PreviewDTO();

        // Set client details
        previewDTO.setClientId(String.valueOf(client.getId()));
        previewDTO.setClientName(client.getFullName());
        previewDTO.setClientAddress(client.getAddress());
        previewDTO.setClientCountry(client.getCountry().toString());
        previewDTO.setClientEmail(client.getEmail());

        // Set provider details
        previewDTO.setProviderName(provider.getFullName()); // Assuming provider's full name is the company name
        previewDTO.setProviderAddress(provider.getAddress());
        previewDTO.setProviderCountry(provider.getCountry().toString());
        previewDTO.setProviderEmail(provider.getEmail());

        // Set invoice details
        previewDTO.setShippingCostType(invoiceFormData.getShippingCostType());
        double reduction = InvoiceCalculator.calculateReduction(invoiceFormData);
        double additionalReduction = InvoiceCalculator.calculateAdditionalReduction(invoiceFormData);
        double totalReduction = reduction + additionalReduction;
        previewDTO.setReduction(reduction);
        previewDTO.setAdditionalReduction(additionalReduction);
        previewDTO.setTotalReduction(totalReduction);
        previewDTO.setShippingCost(InvoiceCalculator.calculateShippingCost(invoiceFormData));
        previewDTO.setAdvancePayment(invoiceFormData.getAdvancePaymentAsDouble());
        previewDTO.setTotalHT(InvoiceCalculator.calculateTotalPrice(invoiceFormData) / (1 + VAT_RATE));
        previewDTO.setTotalTTC(InvoiceCalculator.calculateTotalPrice(invoiceFormData));
        previewDTO.setTva(VAT_RATE * 100);
        previewDTO.setAdditionalReductionType(invoiceFormData.getAdditionalReduction()); // Set the additional reduction type

        // Set product details
        List<PreviewDTO.PreviewProduct> previewProducts = invoiceFormData.getProducts().stream().map(productInvoiceForm -> {
            PreviewDTO.PreviewProduct previewProduct = new PreviewDTO.PreviewProduct();
            ProductCatalog product = products.stream()
                    .filter(p -> p.getId() == Long.parseLong(productInvoiceForm.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productInvoiceForm.getProductId()));
            previewProduct.setProductId(String.valueOf(product.getId()));
            previewProduct.setProductName(product.getName());
            previewProduct.setQuantity(productInvoiceForm.getQuantity());
            previewProduct.setUnitPrice(productInvoiceForm.getPrice());
            previewProduct.setTotalPrice(productInvoiceForm.getQuantity() * productInvoiceForm.getPrice());
            previewProduct.setCurrency(productInvoiceForm.getCurrency());
            return previewProduct;
        }).collect(Collectors.toList());

        previewDTO.setProducts(previewProducts);

        return previewDTO;
    }
    public static InvoiceItemDTO mapToInvoiceItemDTO(Invoice invoice) {
        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setInvoiceId(invoice.getId());
        dto.setCustomerId(invoice.getCustomer().getId());
        dto.setCustomerName(invoice.getCustomer().getFullName());  // Assuming Customer has a getFullName method
        dto.setIssueDate(invoice.getIssueDate());
        return dto;
    }
    public static Invoice previewDtoToInvoice(PreviewDTO previewDTO, User client) {
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

    public static InvoiceDisplayDTO invoiceToInvoiceDisplayDto(Invoice invoice, User provider) {
        InvoiceDisplayDTO dto = new InvoiceDisplayDTO();
        dto.setInvoiceId(invoice.getId());
        dto.setCustomerName(invoice.getCustomer().getFullName());
        dto.setCustomerAddress(invoice.getCustomer().getAddress());
        dto.setCustomerCountry(invoice.getCustomer().getCountry().toString());
        dto.setCustomerEmail(invoice.getCustomer().getEmail());
        dto.setProviderName(provider.getFullName());
        dto.setProviderAddress(provider.getAddress());
        dto.setProviderCountry(provider.getCountry().toString());
        dto.setProviderEmail(provider.getEmail());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setCurrency(invoice.getCurrency());
        dto.setSubtotal(invoice.getSubtotal());
        dto.setDiscount(invoice.getDiscount());
        dto.setAdvancePayment(invoice.getAdvancePayment());
        dto.setTotal(invoice.getTotal());
        dto.setShippingCost(invoice.getShippingCost());
        dto.setShippingCostType(invoice.getShippingCostType());
        dto.setVat(invoice.getVat());
        // Map lines
        dto.setLines(invoice.getLines().stream().map(InvoiceDtoHelper::invoiceLineToInvoiceLineDisplayDto).collect(Collectors.toList()));
        return dto;
    }

    private static InvoiceLineDisplayDTO invoiceLineToInvoiceLineDisplayDto(InvoiceLine line) {
        InvoiceLineDisplayDTO lineDTO = new InvoiceLineDisplayDTO();
        lineDTO.setProductId(line.getProduct().getId());
        lineDTO.setProductName(line.getProduct().getName());
        lineDTO.setQuantity(line.getQuantity());
        lineDTO.setUnitPrice(line.getPrice());
        lineDTO.setTotal(line.getTotal());
        return lineDTO;
    }
}
