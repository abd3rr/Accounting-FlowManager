package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.ClientDTO;
import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoConverter {
    private final RoleService roleService;

    @Autowired
    public ClientDtoConverter(RoleService roleService) {
        this.roleService = roleService;
    }

    public User clientDtoToUser(ClientDTO clientDTO) {
        User user = new User();
        user.setFullName(clientDTO.getFullName());
        user.setFullName(clientDTO.getBusinessName());
        user.setEmail(clientDTO.getEmail());
        user.setPhoneNumber(clientDTO.getPhoneNumber());
        user.setAddress(clientDTO.getAddress());
        user.setCountry(clientDTO.getCountry());
        user.setRole(roleService.getOrCreateRole(clientDTO.getRoleName()));
        // for testing we will have a dto for login
        user.setPasswordHash(clientDTO.getPasswordHash());
        return user;
    }
}
