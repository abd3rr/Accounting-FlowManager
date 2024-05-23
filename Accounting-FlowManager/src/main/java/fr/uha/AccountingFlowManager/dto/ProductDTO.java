package fr.uha.AccountingFlowManager.dto;

import fr.uha.AccountingFlowManager.enums.Currency;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    @NotEmpty(message="Name should not be empty")
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

    @Override
    public String toString() {
        return "ProductDTO{" +
                "\nname='" + name + '\'' +
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
