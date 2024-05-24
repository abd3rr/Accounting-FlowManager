package fr.uha.AccountingFlowManager.dto.invoice;

import lombok.Data;

import java.util.List;

@Data
public class PreviewDTO {
    private String clientId;
    private String clientName;
    private String clientEmail;
    private String providerName;
    private String providerId;
    private String providerEmail;
    private String shippingCostType;
    private double totalReduction;
    private double shippingCost;
    private double tva;
    private double totalHT;
    private double totalTTC;
    private List<PreviewProduct> products;

    @Data
    public static class PreviewProduct {
        private String productName;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private String currency;
    }
}
