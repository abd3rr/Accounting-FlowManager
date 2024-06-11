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
       // user.setPasswordHash(clientDTO.getPasswordHash());
        return user;
    }
    public ClientDTO convertToClientDto(User user) {
        if (user == null) {
            return null;
        }
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(user.getId());
        clientDTO.setFullName(user.getFullName());
        clientDTO.setEmail(user.getEmail());
        clientDTO.setPhoneNumber(user.getPhoneNumber());
        clientDTO.setAddress(user.getAddress());
        clientDTO.setCountry(user.getCountry());
        clientDTO.setRegistrationDate(user.getRegistrationDate());
        clientDTO.setLastUpdated(user.getLastUpdated());
        if (user.getRole() != null) {
            clientDTO.setRoleName(user.getRole().getName());
        }
        return clientDTO;
    }
}
