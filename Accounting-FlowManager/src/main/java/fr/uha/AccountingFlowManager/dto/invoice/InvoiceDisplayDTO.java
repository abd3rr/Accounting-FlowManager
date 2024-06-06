package fr.uha.AccountingFlowManager.dto.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDisplayDTO {
    private long invoiceId;
    private String customerName;
    private String customerAddress;
    private String customerCountry;
    private String customerEmail;
    private String providerName;
    private String providerAddress;
    private String providerCountry;
    private String providerEmail;
    private boolean fileExist = false;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss", timezone = "Europe/Paris")
    private LocalDateTime issueDate;

    private Currency currency;
    private double subtotal;
    private double discount;
    private double advancePayment;
    private double total;
    private double shippingCost;
    private ShippingCostType shippingCostType;
    private double vat;
    private List<InvoiceLineDisplayDTO> lines;
}

