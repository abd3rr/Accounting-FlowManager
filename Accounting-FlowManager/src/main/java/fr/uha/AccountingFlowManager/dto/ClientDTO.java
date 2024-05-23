package fr.uha.AccountingFlowManager.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import fr.uha.AccountingFlowManager.enums.Country;
import fr.uha.AccountingFlowManager.enums.RoleName;
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

    private Country country;

    private LocalDateTime registrationDate;

    private LocalDateTime lastUpdated;
    private RoleName roleName;

    private String passwordHash;
    //@JsonBackReference
    //private List<ProviderDTO> providers;



}
