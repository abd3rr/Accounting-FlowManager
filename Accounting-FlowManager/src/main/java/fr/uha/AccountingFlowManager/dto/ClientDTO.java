package fr.uha.AccountingFlowManager.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientDTO {
    private long id;

    private String fullName;

    private String businessName;

    private String email;

    private String phoneNumber;

    private String address;

    private String companyName;

    private String websiteUrl;

    private String country;

    private LocalDateTime registrationDate;

    private LocalDateTime lastUpdated;

    @JsonManagedReference
    private List<ProviderDTO> providers;

}
