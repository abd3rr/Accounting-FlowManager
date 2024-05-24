package fr.uha.AccountingFlowManager.dto.invoice;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceFormDataDTO {
    private String clientId;
    private String shippingCostType;
    private String reduction;
    private String additionalReduction;
    private List<ProductInvoiceForm> products;

    @Data
    public static
    class ProductInvoiceForm {
        private String productId;
        private int quantity;
        private double price;
        private String currency;
    }
}
