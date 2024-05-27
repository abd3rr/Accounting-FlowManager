package fr.uha.AccountingFlowManager.util;

import fr.uha.AccountingFlowManager.dto.ClientDTO;
import fr.uha.AccountingFlowManager.dto.ProviderDTO;
import fr.uha.AccountingFlowManager.enums.Country;
import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DatabaseSeeding implements CommandLineRunner {
    private final UserService userService;
    @Autowired
    public DatabaseSeeding(UserService userService){
        this.userService = userService;
    }

    private void addTestUsers() {
        String passwordHash = "$2a$10$UDo8bnwEVZoWIZ2ceQ2Mt.Of9w4pwWaSTWyd9FLhuvKSIh2du.Zru";

        // Define test clients
        ClientDTO client1 = new ClientDTO();
        client1.setFullName("Client One");
        client1.setEmail("client1@gmail.com");
        client1.setPhoneNumber("123-456-7890");
        client1.setAddress("1234 Client St.");
        client1.setCountry(Country.NORWAY);
     //   client1.setPasswordHash(passwordHash);
        client1.setRoleName(RoleName.ROLE_CLIENT);

        ClientDTO client2 = new ClientDTO();
        client2.setFullName("Client Two");
        client2.setEmail("client2@gmail.com");
        client2.setPhoneNumber("234-567-8901");
        client2.setAddress("2345 Client Ave.");
        client2.setCountry(Country.NORWAY);
        //client2.setPasswordHash(passwordHash);
        client2.setRoleName(RoleName.ROLE_CLIENT);

        ClientDTO client3 = new ClientDTO();
        client3.setFullName("Client Three");
        client3.setEmail("client3@gmail.com");
        client3.setPhoneNumber("345-678-9012");
        client3.setAddress("3456 Client Blvd.");
        client3.setCountry(Country.NORWAY);
        //client3.setPasswordHash(passwordHash);
        client3.setRoleName(RoleName.ROLE_CLIENT);

        // Define test providers
        ProviderDTO provider1 = new ProviderDTO();
        provider1.setCompanyName("Provider One");
        provider1.setEmail("provider1@gmail.com");
        provider1.setPhoneNumber("456-789-0123");
        provider1.setAddress("1234 Provider Rd.");
        provider1.setWebsiteUrl("http://provider1.com");
        provider1.setCountry(Country.NORWAY);
        provider1.setPasswordHash(passwordHash);
        provider1.setRoleName(RoleName.ROLE_PROVIDER);

        ProviderDTO provider2 = new ProviderDTO();
        provider2.setCompanyName("Provider Two");
        provider2.setEmail("provider2@gmail.com");
        provider2.setPhoneNumber("567-890-1234");
        provider2.setAddress("2345 Provider Lane");
        provider2.setWebsiteUrl("http://provider2.com");
        provider2.setCountry(Country.NORWAY);
        provider2.setPasswordHash(passwordHash);
        provider2.setRoleName(RoleName.ROLE_PROVIDER);


        // Add clients and providers to the database
        Long clientId1 = userService.addClient(client1);
        Long clientId2 = userService.addClient(client2);
        Long clientId3 = userService.addClient(client3);
        Long providerId1 = userService.addProvider(provider1);
        Long providerId2 = userService.addProvider(provider2);

        // Assuming you have methods to retrieve IDs once users are saved
        List<Long> clientIdsForProvider1 = List.of(
                clientId1,
                clientId2,
                clientId3
        );
        List<Long> clientIdsForProvider2 = List.of(
                clientId2,
                clientId3
        );

        // Add relationships
        userService.addProviderClients(providerId1, clientIdsForProvider1);
        userService.addProviderClients(providerId2, clientIdsForProvider2);
    }

    @Override
    public void run(String... args) throws Exception {
        // for testing purposes
       //addTestUsers();
    }
}
