package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.ClientDTO;
import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.dto.client.ProviderItemDTO;
import fr.uha.AccountingFlowManager.dto.registerForm.ProviderInfosDTO;
import fr.uha.AccountingFlowManager.dto.registerForm.RegistrationDTO;
import fr.uha.AccountingFlowManager.enums.Country;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.ProductCatalog;
import fr.uha.AccountingFlowManager.model.Role;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.ProductRepository;
import fr.uha.AccountingFlowManager.repository.RoleRepository;
import fr.uha.AccountingFlowManager.repository.UserRepository;
import fr.uha.AccountingFlowManager.security.CustomUserDetails;
import fr.uha.AccountingFlowManager.util.ClientDtoConverter;
import fr.uha.AccountingFlowManager.util.ProviderDtoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProviderDtoConverter providerDtoConverter;
    private final ClientDtoConverter clientDtoConverter;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Autowired
    public UserService(UserRepository userRepository, ProviderDtoConverter providerDtoConverter, ClientDtoConverter clientDtoConverter, ProductRepository productRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RoleService roleService) {
        this.userRepository = userRepository;
        this.providerDtoConverter = providerDtoConverter;
        this.clientDtoConverter = clientDtoConverter;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
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

    @Transactional(readOnly = true)
    public List<ProviderItemDTO> getClientProviders(Long clientId) {
        // Fetch the client from the database
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        // Ensure the client has the correct role if necessary
        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        // Check if the client has providers and fetch them
        if (client.getProviders() == null || client.getProviders().isEmpty()) {
            return Collections.emptyList();  // Return an empty list if no providers are associated
        }

        // Map the list of providers to ProviderItemDTO
        return client.getProviders().stream()
                .map(provider -> {
                    ProviderItemDTO dto = new ProviderItemDTO();
                    dto.setId(provider.getId());
                    dto.setFullName(provider.getFullName());
                    dto.setEmail(provider.getEmail());
                    dto.setAddress(provider.getAddress());
                    dto.setCountry(provider.getCountry());
                    return dto;
                })
                .collect(Collectors.toList());
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
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        List<User> providers = userRepository.findAllById(providerIds);

        for (User provider : providers) {
            if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
                throw new IllegalArgumentException("User with ID: " + provider.getId() + " is not a provider");
            }
        }

        if (client.getProviders() == null) {
            client.setProviders(new ArrayList<>());
        }
        client.getProviders().addAll(providers);

        // Add the client to each provider's list of clients
        for (User provider : providers) {
            if (provider.getClients() == null) {
                provider.setClients(new ArrayList<>());
            }
            provider.getClients().add(client);
        }

        userRepository.save(client);
        userRepository.saveAll(providers);
    }

    @Transactional(readOnly = true)
    public List<ClientDTO> getCurrentProviderClients() {
        Long currentUserId = getCurrentUserId();
        String currentUserRole = getCurrentUserRole();

        if (currentUserRole == null || !currentUserRole.equals(RoleName.ROLE_PROVIDER.toString())) {
            throw new IllegalArgumentException("Current user is not a provider");
        }

        List<User> clients = userRepository.findClientsByProviderId(currentUserId);

        return clients.stream()
                .filter(client -> client.getPasswordHash() != null && !client.getPasswordHash().isEmpty())
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

        return productRepository.findByProvider_IdAndIsListedTrue(currentUserId);
    }

    // for register client form
    @Transactional(readOnly = true)
    public List<ProviderInfosDTO> getAllProviders() {
        Role providerRole = roleRepository.findByName(RoleName.ROLE_PROVIDER)
                .orElseThrow(() -> new IllegalArgumentException("Role 'ROLE_PROVIDER' not found"));

        List<User> providers = userRepository.findUsersByRole(providerRole);

        return providers.stream()
                .map(this::userToProviderInfosDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProviderInfosDTO> getAllProvidersExcludingClients(Long clientId) {
        // Fetch the client from the database and its providers
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        Set<User> currentProviders = new HashSet<>(client.getProviders());

        // Fetch the provider role
        Role providerRole = roleRepository.findByName(RoleName.ROLE_PROVIDER)
                .orElseThrow(() -> new IllegalArgumentException("Role 'ROLE_PROVIDER' not found"));

        // Fetch all providers excluding those who are already the client's providers
        List<User> providers = userRepository.findUsersByRole(providerRole)
                .stream()
                .filter(provider -> !currentProviders.contains(provider))
                .toList();

        // Convert to DTOs
        return providers.stream()
                .map(this::userToProviderInfosDTO)
                .collect(Collectors.toList());
    }

    private ProviderInfosDTO userToProviderInfosDTO(User user) {
        ProviderInfosDTO dto = new ProviderInfosDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        return dto;
    }


    @Transactional
    public User registerNewUser(RegistrationDTO registrationDTO) {
        try {
            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(registrationDTO.getEmail());
            if (existingUser.isPresent()) {
                System.out.println("Error: Email already in use - " + registrationDTO.getEmail());
                throw new IllegalArgumentException("Email already in use");
            }

            User newUser = new User();
            newUser.setFullName(registrationDTO.getFullName());
            newUser.setEmail(registrationDTO.getEmail());
            newUser.setPasswordHash(passwordEncoder.encode(registrationDTO.getPassword()));
            newUser.setAddress(registrationDTO.getAddress());
            newUser.setPhoneNumber(registrationDTO.getPhoneNumber());

            try {
                newUser.setCountry(Country.valueOf(registrationDTO.getCountry()));
            } catch (IllegalArgumentException e) {
                System.out.println("Error: Invalid country specified - " + registrationDTO.getCountry());
                throw new IllegalArgumentException("Invalid country");
            }

            Role clientRole = roleService.getOrCreateRole(RoleName.ROLE_CLIENT);
            newUser.setRole(clientRole);

            User savedUser = userRepository.save(newUser);
            System.out.println("User saved successfully: " + savedUser.toString());

            // After saving the user, add providers if any
            if (registrationDTO.getProviders() != null && !registrationDTO.getProviders().isEmpty()) {
                addClientProviders(savedUser.getId(), registrationDTO.getProviders());
            }

            return savedUser;
        } catch (DataIntegrityViolationException e) {
            System.out.println("Database error during user save: " + e.getMessage());
            throw new IllegalStateException("Error saving user", e);
        } catch (Exception e) {
            System.out.println("General error during registration: " + e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteProviderForClient(Long clientId, Long providerId) {
        // Fetch the client from the database
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        // Ensure the client has the correct role
        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        // Fetch the provider from the database
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        // Ensure the provider has the correct role
        if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
            throw new IllegalArgumentException("The specified user is not a provider");
        }

        // Remove the provider from the client's list of providers
        client.getProviders().remove(provider);

        // Remove the client from the provider's list of clients
        provider.getClients().remove(client);

        // Save the updated client and provider
        userRepository.save(client);
        userRepository.save(provider);
    }
}

