package fr.uha.AccountingFlowManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "invoice_lines")
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductCatalog product;


    @Size(min = 0)
    private double quantity;

    @Size(min = 0)
    private double price;

    @Size(min = 0)
    private double total;  // price * quantity

    @PostLoad
    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        total = price * quantity;
    }
}
