package fr.uha.AccountingFlowManager.dto.client;

import fr.uha.AccountingFlowManager.enums.Country;
import lombok.Data;

@Data
public class ProviderItemDTO {
    Long id;
    String fullName;
    String email;
    String address;
    Country country;
}
