package fr.uha.AccountingFlowManager.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProviderDTO {
    private long id;

    private String companyName;

    private String email;

    private String phoneNumber;

    private String address;

    private String websiteUrl;

    private String country;

    private LocalDateTime registrationDate;

    private LocalDateTime lastUpdated;

    @JsonBackReference
    private List<ClientDTO> clients;

    private List<ProductCatalogDTO> products;

}
