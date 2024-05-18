package fr.uha.AccountingFlowManager.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import fr.uha.AccountingFlowManager.enums.RoleName;
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

    private fr.uha.AccountingFlowManager.enums.Country country;

    private LocalDateTime registrationDate;

    private LocalDateTime lastUpdated;
    private String passwordHash;
    private RoleName roleName;

    @JsonManagedReference
    private List<ClientDTO> clients;

    private List<ProductCatalogDTO> products;

}
