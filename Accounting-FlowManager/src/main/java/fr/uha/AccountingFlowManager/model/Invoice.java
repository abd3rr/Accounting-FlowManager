package fr.uha.AccountingFlowManager.model;

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

    @PrePersist
    public void setIssueDate() {
        this.issueDate = LocalDateTime.now();
    }

    @PreUpdate
    public void updateSubtotalAndTotal() {
        this.subtotal = lines.stream().mapToDouble(InvoiceLine::getTotal).sum();
        this.total = subtotal - discount - advancePayment;  // Calculate the final total
    }
}
