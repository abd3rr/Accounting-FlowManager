package fr.uha.AccountingFlowManager.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uha.AccountingFlowManager.dto.invoice.*;
import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.InvoiceLine;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InvoiceDtoHelper {

    private static final double VAT_RATE = 0.15;
    private static final ObjectMapper objectMapper = new ObjectMapper();


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
        double totalHT = InvoiceCalculator.calculateHT(invoiceFormData);
        double reduction = InvoiceCalculator.calculateReductionRate(invoiceFormData);
        double additionalReduction = InvoiceCalculator.calculateAdditionalReductionRate(invoiceFormData);
        double totalReduction = reduction + additionalReduction;
        double shippingCost = InvoiceCalculator.calculateShippingCost(invoiceFormData);
        double advancePayment = invoiceFormData.getAdvancePaymentAsDouble();
        double totalTTC = InvoiceCalculator.calculateTTC(invoiceFormData);

        // Set calculated values to DTO
        previewDTO.setReduction(reduction);
        previewDTO.setAdditionalReduction(additionalReduction);
        previewDTO.setTotalReduction(totalReduction);
        previewDTO.setShippingCost(shippingCost);
        previewDTO.setAdvancePayment(advancePayment);
        previewDTO.setTotalHT(totalHT);
        previewDTO.setTotalTTC(totalTTC);
        previewDTO.setTva(VAT_RATE * 100); // Assuming VAT_RATE is statically imported or defined
        previewDTO.setAdditionalReductionType(invoiceFormData.getAdditionalReduction());

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

    public static Invoice previewDtoToInvoice(PreviewDTO previewDTO, User client, User provider) {
        Invoice invoice = new Invoice();
        invoice.setCustomer(client);
        invoice.setCurrency(Currency.EUR);
        invoice.setSubtotal(previewDTO.getTotalHT());
        invoice.setDiscount(previewDTO.getTotalReduction());
        invoice.setAdvancePayment(previewDTO.getAdvancePayment());
        invoice.setShippingCost(previewDTO.getShippingCost());
        invoice.setShippingCostType(previewDTO.getShippingCostType());
        invoice.setProvider(provider);

        invoice.setVat(previewDTO.getTotalHT() * (previewDTO.getTva() / 100));

        // Set total which now includes recalculated VAT

        invoice.setTotal(previewDTO.getTotalTTC());

        return invoice;
    }

    private static double calculateTaxableAmount(Invoice invoice) {
        return invoice.getSubtotal() - invoice.getDiscount() + invoice.getShippingCost() - invoice.getAdvancePayment();
    }

    private static double calculateVAT(double taxableAmount) {
        final double VAT_RATE = 0.15; // 15% VAT
        return taxableAmount * VAT_RATE;
    }

    private static double calculateTotal(Invoice invoice, double vat) {
        return invoice.getSubtotal() - invoice.getDiscount() + invoice.getShippingCost() - invoice.getAdvancePayment() + vat;
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
        if (invoice.getFile() != null) dto.setFileExist(true);
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

    public static InvoiceDisplayDTO uploadedInvoiceToDisplayDTO(String json, User provider) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode contentNode = rootNode.path("choices").path(0).path("message").path("content");

            // Parse the content as a separate JSON
            JsonNode invoiceNode = objectMapper.readTree(contentNode.asText());

            InvoiceDisplayDTO dto = new InvoiceDisplayDTO();

            dto.setCustomerName(getJsonNodeValue(invoiceNode, "Customer Name"));
            dto.setCustomerAddress(getJsonNodeValue(invoiceNode, "Customer Address"));
            dto.setCustomerCountry(getJsonNodeValue(invoiceNode, "Customer Country"));
            dto.setCustomerEmail(getJsonNodeValue(invoiceNode, "Customer Email"));

            String issueDateStr = getJsonNodeValue(invoiceNode, "Issue Date");
            if (!issueDateStr.isEmpty()) {
                LocalDateTime issueDate = parseDateTime(issueDateStr);
                dto.setIssueDate(issueDate);
            } else {
                dto.setIssueDate(LocalDateTime.now());
            }

            dto.setCurrency(Currency.EUR);
            dto.setSubtotal(parseDouble(getJsonNodeValue(invoiceNode, "Subtotal")));
            dto.setDiscount(parseDouble(getJsonNodeValue(invoiceNode, "Discount")));
            dto.setAdvancePayment(parseDouble(getJsonNodeValue(invoiceNode, "Advance Payment")));
            dto.setTotal(parseDouble(getJsonNodeValue(invoiceNode, "Total")));
            dto.setShippingCost(parseDouble(getJsonNodeValue(invoiceNode, "Shipping Cost")));
            dto.setVat(parseDouble(getJsonNodeValue(invoiceNode, "VAT")));

            // Parse product lines
            List<InvoiceLineDisplayDTO> lines = new ArrayList<>();
            JsonNode linesNode = invoiceNode.path("Lines");
            if (linesNode.isArray()) {
                for (JsonNode lineNode : linesNode) {
                    InvoiceLineDisplayDTO line = new InvoiceLineDisplayDTO();
                    line.setProductName(getJsonNodeValue(lineNode, "Product Name"));
                    line.setQuantity(parseDouble(getJsonNodeValue(lineNode, "Quantity")));
                    line.setUnitPrice(parseDouble(getJsonNodeValue(lineNode, "Unit Price")));
                    line.setTotal(parseDouble(getJsonNodeValue(lineNode, "Total Price")));
                    lines.add(line);
                }
            }
            dto.setLines(lines);

            // Set provider information
            dto.setProviderName(provider.getFullName());
            dto.setProviderAddress(provider.getAddress());
            dto.setProviderCountry(provider.getCountry().toString());
            dto.setProviderEmail(provider.getEmail());

            return dto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to parse json: " + json, e);
        }
    }

    private static String getJsonNodeValue(JsonNode node, String fieldName) {
        JsonNode valueNode = node.path(fieldName);
        if (valueNode.isMissingNode() || valueNode.isNull()) {
            System.err.println("Missing or null field: " + fieldName);
            return "";
        }
        String value = valueNode.asText().trim();
        System.out.println("Extracted field " + fieldName + ": " + value);
        return value;
    }

    private static double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0.0;
        }
        try {
            // Remove any space characters
            value = value.replaceAll("\\s", "");

            // Remove the thousands separator (comma)
            value = value.replace(",", "");

            System.out.println("Parsed double: " + value);
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid double value: " + value);
            return 0.0;
        }
    }

    private static LocalDateTime parseDateTime(String dateTimeStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                DateTimeFormatter.ISO_LOCAL_DATE
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e) {
                // Try the next formatter
            }
        }

        // If none of the formatters worked, throw an exception
        throw new IllegalArgumentException("Invalid date format: " + dateTimeStr);
    }
}
