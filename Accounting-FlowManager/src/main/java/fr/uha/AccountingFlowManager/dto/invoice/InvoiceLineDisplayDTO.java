package fr.uha.AccountingFlowManager.dto.invoice;

import lombok.Data;

@Data
public class InvoiceLineDisplayDTO {
    private long productId;
    private String productName;
    private double quantity;
    private double unitPrice;
    private double total;
}
