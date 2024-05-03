package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ProductCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String name;

    private String description;

    @NotBlank
    @Size(min = 0)
    private double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Duration duration;

    private LocalDateTime dateAdded;

    private LocalDateTime lastUpdated;

    @PrePersist
    public void setDateAdded() {
        this.dateAdded = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

}
