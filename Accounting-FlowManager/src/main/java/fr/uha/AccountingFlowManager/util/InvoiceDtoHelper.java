package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.invoice.InvoiceFormDataDTO;
import fr.uha.AccountingFlowManager.dto.invoice.InvoiceItemDTO;
import fr.uha.AccountingFlowManager.dto.invoice.PreviewDTO;
import fr.uha.AccountingFlowManager.model.Invoice;
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
}
