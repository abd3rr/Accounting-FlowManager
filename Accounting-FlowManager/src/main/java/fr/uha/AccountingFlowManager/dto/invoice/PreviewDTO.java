package fr.uha.AccountingFlowManager.dto.invoice;

import fr.uha.AccountingFlowManager.enums.ReductionType;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import lombok.Data;

import java.util.List;

@Data
public class PreviewDTO {
    private String clientId;
    private String clientName;
    private String clientAddress;
    private String clientCountry;
    private String clientEmail;
    private String providerName;
    private String providerAddress;
    private String providerCountry;
    private String providerEmail;
    private ShippingCostType shippingCostType;
    private double reduction;
    private double additionalReduction;
    private ReductionType additionalReductionType;
    private double totalReduction;
    private double shippingCost;
    private double advancePayment;
    private double tva = 15;
    private double totalHT;
    private double totalTTC;
    private List<PreviewProduct> products;

    @Data
    public static class PreviewProduct {
        private String productId;
        private String productName;
        private int quantity;
        private double unitPrice;
        private double totalPrice;
        private String currency;
    }
}
