package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.dto.AccountTransactionsDTO;
import fr.uha.AccountingFlowManager.dto.ClientDTO;
import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.dto.client.ProviderItemDTO;
import fr.uha.AccountingFlowManager.dto.registerForm.ProviderInfosDTO;
import fr.uha.AccountingFlowManager.dto.registerForm.RegistrationDTO;
import fr.uha.AccountingFlowManager.enums.AccountType;
import fr.uha.AccountingFlowManager.enums.Country;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.*;
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
    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public UserService(UserRepository userRepository, ProviderDtoConverter providerDtoConverter, ClientDtoConverter clientDtoConverter, ProductRepository productRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, RoleService roleService, AccountService accountService, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.providerDtoConverter = providerDtoConverter;
        this.clientDtoConverter = clientDtoConverter;
        this.productRepository = productRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getId();
        }
        return null;
    }

    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(null);
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
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        if (client.getProviders() == null || client.getProviders().isEmpty()) {
            return Collections.emptyList();  // Return an empty list if no providers are associated
        }

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
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
            throw new IllegalArgumentException("The specified user is not a provider");
        }

        List<User> potentialClients = userRepository.findAllById(clientIds);

        for (User client : potentialClients) {
            if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
                throw new IllegalArgumentException("User with ID: " + client.getId() + " is not a client");
            }
        }

        Set<User> updatedClients = new HashSet<>(provider.getClients());
        updatedClients.addAll(potentialClients); // Add new clients, duplicates will not be added

        provider.getClients().clear();
        provider.getClients().addAll(updatedClients);

        for (User client : updatedClients) {
            Set<User> providersSet = new HashSet<>(client.getProviders());
            providersSet.add(provider); // Add the provider to the client's providers list
            client.getProviders().clear();
            client.getProviders().addAll(providersSet);
        }

        userRepository.save(provider);
        userRepository.saveAll(updatedClients);
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
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        Set<User> currentProviders = new HashSet<>(client.getProviders());

        Role providerRole = roleRepository.findByName(RoleName.ROLE_PROVIDER)
                .orElseThrow(() -> new IllegalArgumentException("Role 'ROLE_PROVIDER' not found"));

        List<User> providers = userRepository.findUsersByRole(providerRole)
                .stream()
                .filter(provider -> !currentProviders.contains(provider))
                .toList();

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
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));

        if (!client.getRole().getName().equals(RoleName.ROLE_CLIENT)) {
            throw new IllegalArgumentException("The specified user is not a client");
        }

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new IllegalArgumentException("Provider not found with ID: " + providerId));

        if (!provider.getRole().getName().equals(RoleName.ROLE_PROVIDER)) {
            throw new IllegalArgumentException("The specified user is not a provider");
        }

        client.getProviders().remove(provider);

        provider.getClients().remove(client);

        userRepository.save(client);
        userRepository.save(provider);
    }

    @Transactional
    public AccountTransactionsDTO getAccountTransactionsDTOByUser(User user) {
        Account account = accountService.findOrCreateAccountByProviderAndType(user, AccountType.BALANCE);
        List<Transaction> transactions = transactionService.getTransactionsForProvider(user);

        List<AccountTransactionsDTO.TransactionDTO> transactionDTOs = transactions.stream()
                .map(transaction -> new AccountTransactionsDTO.TransactionDTO(
                        transaction.getDate(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getDescription()
                ))
                .collect(Collectors.toList());

        return new AccountTransactionsDTO(account.getBalance(), transactionDTOs);
    }
}

