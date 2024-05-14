package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.Currency;
import fr.uha.AccountingFlowManager.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    @Size(min = 0)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private String description;

    @ManyToOne
    private Account account; // link to one account that's affected by the transaction

    @ManyToOne
    private User provider;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

}
