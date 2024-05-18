package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProviderDtoConverter {
    private final RoleService roleService;

    @Autowired
    public ProviderDtoConverter(RoleService roleService) {
        this.roleService = roleService; // Corrected assignment
    }

    public User providerDtoToUser(ProviderDTO providerDTO) {
        User user = new User();
        user.setFullName(providerDTO.getCompanyName());
        user.setEmail(providerDTO.getEmail());
        user.setPhoneNumber(providerDTO.getPhoneNumber());
        user.setAddress(providerDTO.getAddress());
        user.setWebsiteUrl(providerDTO.getWebsiteUrl());
        user.setCompanyName(providerDTO.getCompanyName());
        user.setCountry(providerDTO.getCountry());
        user.setRole(roleService.getOrCreateRole(providerDTO.getRoleName()));
        // for testing we will have a dto for login
        user.setPasswordHash(providerDTO.getPasswordHash());
        return user;
    }
}
