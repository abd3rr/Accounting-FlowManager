package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.ShippingCostType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceLine> lines;  // List of invoice lines

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private fr.uha.AccountingFlowManager.enums.Currency currency;


    @Size(min = 0)
    private double subtotal;  // Total before discounts and taxes
    @Size(min = 0)
    private double discount;  // Total discounts
    @Size(min = 0)
    private double advancePayment;  // Amount paid in advance (acomptes)
    @Size(min = 0)
    private double total;  // Total after all adjustments

    private double shippingCost;

    @Enumerated(EnumType.STRING)
    private fr.uha.AccountingFlowManager.enums.ShippingCostType shippingCostType;  // Enum for shipping cost type

    @Size(min = 0)
    private double vat;  // VAT amount

    @PrePersist
    public void setIssueDate() {
        this.issueDate = LocalDateTime.now();
    }

    @PreUpdate
    public void updateSubtotalAndTotal() {
        this.subtotal = lines.stream().mapToDouble(InvoiceLine::getTotal).sum();
        calculateShippingCost();
        calculateVAT();
        this.total = subtotal - discount - advancePayment + shippingCost + vat;  // Calculate the final total including VAT
    }

    private void calculateShippingCost() {
        switch (shippingCostType) {
            case PROVIDER_PAYS:
                shippingCost = 0;  // Provider pays, so no cost to the customer
                break;
            case FLAT_RATE:
                shippingCost = 5;  // Flat rate contribution, e.g., 5 euros
                break;
            case FULL_BILLING:
                shippingCost = lines.stream().mapToDouble(line -> line.getQuantity() * 0.10).sum();  // Full billing to the customer, e.g., 10 cents per item
                break;
        }
    }

    private void calculateVAT() {
        double taxableAmount = subtotal - discount - advancePayment + shippingCost;  // Taxable amount includes shipping cost
        this.vat = taxableAmount * 0.15;  // VAT at 15%
    }
}
