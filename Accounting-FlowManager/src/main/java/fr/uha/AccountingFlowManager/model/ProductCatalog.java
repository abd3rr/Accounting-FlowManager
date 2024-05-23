package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.Currency;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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


    @Min(0)
    private double price;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private Duration duration;

    private LocalDateTime dateAdded;

    private LocalDateTime lastUpdated;

    private boolean isService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id")
    private User provider;  // Update this field to ManyToOne

    @PrePersist
    public void setDateAdded() {
        this.dateAdded = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }


    @Override
    public String toString() {
        return "\nProductCatalog{" +
                "\nid=" + id +
                "\n, name='" + name + '\'' +
                "\n, description='" + description + '\'' +
                "\n, price=" + price +
                "\n, currency=" + currency +
                "\n, duration=" + duration +
                "\n, dateAdded=" + dateAdded +
                "\n, lastUpdated=" + lastUpdated +
                "\n, isService=" + isService +
                "\n}";
    }
}
