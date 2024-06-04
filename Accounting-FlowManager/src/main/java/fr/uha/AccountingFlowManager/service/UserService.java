package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.ClientDTO;
import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.ProductRepository;
import fr.uha.AccountingFlowManager.repository.UserRepository;
import fr.uha.AccountingFlowManager.security.CustomUserDetails;
import fr.uha.AccountingFlowManager.util.ClientDtoConverter;
import fr.uha.AccountingFlowManager.util.ProviderDtoConverter;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProviderDtoConverter providerDtoConverter;
    private final ClientDtoConverter clientDtoConverter;

    @Autowired
    public UserService(UserRepository userRepository, ProviderDtoConverter providerDtoConverter, ClientDtoConverter clientDtoConverter, ProductRepository productRepository){
        this.userRepository = userRepository;
        this.providerDtoConverter = providerDtoConverter;
        this.clientDtoConverter = clientDtoConverter;
        this.productRepository = productRepository;
    }
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return null; // or throw an appropriate exception
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null); // or throw an appropriate exception
        }
        return null;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByFullName(String fullName) {
        return userRepository.findByFullName(fullName).orElse(null);
    }
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername();
        }
        return null;
    }

    @Transactional
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    @Transactional
    public Long addProvider(ProviderDTO providerDTO) {
        User user = providerDtoConverter.providerDtoToUser(providerDTO);
        return userRepository.save(user).getId();
    }

    @Transactional
    public Long addClient(ClientDTO clientDTO) {
        User user = clientDtoConverter.clientDtoToUser(clientDTO);
        return userRepository.save(user).getId();
    }

    @Transactional
    public void addProviderClients(Long providerId, List<Long> clientIds) {
        // Fetch the provider from the database
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        // Ensure the provider has the correct role if necessary
        if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
            throw new IllegalArgumentException("The specified user is not a provider");
        }

        // Fetch all potential clients by their IDs
        List<User> potentialClients = userRepository.findAllById(clientIds);

        // Validate all fetched users to ensure they are clients
        for (User client : potentialClients) {
            if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
                throw new IllegalArgumentException("User with ID: " + client.getId() + " is not a client");
            }
        }

        // Create a set from the existing clients to avoid duplicates
        Set<User> updatedClients = new HashSet<>(provider.getClients());
        updatedClients.addAll(potentialClients); // Add new clients, duplicates will not be added

        // Update the provider's client list
        provider.getClients().clear();
        provider.getClients().addAll(updatedClients);

        // For each client, add this provider to their list of providers if not already present
        for (User client : updatedClients) {
            Set<User> providersSet = new HashSet<>(client.getProviders());
            providersSet.add(provider); // Add the provider to the client's providers list
            client.getProviders().clear();
            client.getProviders().addAll(providersSet);
        }

        // Save the updated provider and clients
        userRepository.save(provider);
        userRepository.saveAll(updatedClients); // This assumes clients need to be explicitly saved
    }

    @Transactional
    public void addClientProviders(Long clientId, List<Long> providerIds) {
        // Fetch the client from the database
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        // Ensure the client has the correct role if necessary
        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        // Fetch all potential providers by their IDs
        List<User> potentialProviders = userRepository.findAllById(providerIds);

        // Validate all fetched users to ensure they are providers
        for (User provider : potentialProviders) {
            if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
                throw new IllegalArgumentException("User with ID: " + provider.getId() + " is not a provider");
            }
        }

        // Create a set from the existing providers to avoid duplicates
        Set<User> updatedProviders = new HashSet<>(client.getProviders());
        updatedProviders.addAll(potentialProviders); // Add new providers, duplicates will not be added

        // Update the client's provider list
        client.getProviders().clear();
        client.getProviders().addAll(updatedProviders);

        // For each provider, add this client to their list of clients if not already present
        for (User provider : updatedProviders) {
            Set<User> clientsSet = new HashSet<>(provider.getClients());
            clientsSet.add(client); // Add the client to the provider's clients list
            provider.getClients().clear();
            provider.getClients().addAll(clientsSet);
        }

        // Save the updated client and providers
        userRepository.save(client);
        userRepository.saveAll(updatedProviders); // This ensures all providers are updated in the database
    }

    @Transactional( readOnly = true)
    public List<ClientDTO> getCurrentProviderClients() {
        Long currentUserId = getCurrentUserId();
        String currentUserRole = getCurrentUserRole();

        if (currentUserRole == null || !currentUserRole.equals(RoleName.ROLE_PROVIDER.toString())) {
            throw new IllegalArgumentException("Current user is not a provider");
        }

        User provider = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + currentUserId));

        return provider.getClients().stream()
                .map(clientDtoConverter::convertToClientDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductCatalog> getCurrentProviderProducts() {
        Long currentUserId = getCurrentUserId();
        String currentUserRole = getCurrentUserRole();

        if (currentUserRole == null || !currentUserRole.equals(RoleName.ROLE_PROVIDER.toString())) {
            throw new IllegalArgumentException("Current user is not a provider");
        }

        return productRepository.findByProvider_Id(currentUserId);
    }



}

