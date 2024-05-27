package fr.uha.AccountingFlowManager.dto.invoice;

import fr.uha.AccountingFlowManager.exception.InvoiceExceptions;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceFormDataDTO {
    private String clientId;
    private String shippingCostType;
    private String reduction;
    private String additionalReduction;
    private String advancePayment;
    private List<ProductInvoiceForm> products;

    @Data
    public static
    class ProductInvoiceForm {
        private String productId;
        private int quantity;
        private double price;
        private String currency;


    }
    public double getAdvancePaymentAsDouble() {
        if (advancePayment == null || advancePayment.isEmpty()) {
            return 0.0; // Default to 0 if not specified
        }
        try {
            return Double.parseDouble(advancePayment);
        } catch (NumberFormatException e) {
            throw new InvoiceExceptions.InvalidAdvancementPaymentException("Invalid advance payment format: " + advancePayment);
        }
    }
}
