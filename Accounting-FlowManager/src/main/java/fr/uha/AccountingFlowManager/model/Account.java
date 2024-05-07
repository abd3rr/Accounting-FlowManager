package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Size(min = 0)
    private Double balance = 0.0;

    @ManyToOne
    private Provider provider;


}
