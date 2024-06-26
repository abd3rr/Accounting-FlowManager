package fr.uha.AccountingFlowManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private User provider;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InvoiceLine> lines;  // List of invoice lines

    private LocalDateTime issueDate;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private fr.uha.AccountingFlowManager.enums.Currency currency;

    @DecimalMin(value = "0.0", inclusive = true)
    private double subtotal;  // Total before discounts and taxes

    @DecimalMin(value = "0.0", inclusive = true)
    private double discount;  // Total discounts

    @DecimalMin(value = "0.0", inclusive = true)
    private double advancePayment;  // Amount paid in advance (acomptes)

    @DecimalMin(value = "0.0", inclusive = true)
    private double total;  // Total after all adjustments

    private double shippingCost;

    @Enumerated(EnumType.STRING)
    private fr.uha.AccountingFlowManager.enums.ShippingCostType shippingCostType;  // Enum for shipping cost type

    @DecimalMin(value = "0.0", inclusive = true)
    private double vat;  // VAT amount

    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private File file;

    @PrePersist
    public void setIssueDate() {
        this.issueDate = LocalDateTime.now();
    }
/*
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
                shippingCost = 0;
                break;
            case FLAT_RATE:
                shippingCost = 5;
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

 */
}
