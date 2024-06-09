package fr.uha.AccountingFlowManager.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String fullName;

    @Email(message = "Email should be valid")
    private String email;

    private String passwordHash;

    @Pattern(regexp = "^$|^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Invoice> invoices; // Invoices can be associated with both users and providers

    private String companyName;  // Nullable, used if the user is a business or a provider
    private String websiteUrl;  // Nullable, used if the user is a provider

    @Enumerated(EnumType.STRING)
    private fr.uha.AccountingFlowManager.enums.Country country;  // Common for all users

    private LocalDateTime registrationDate;  // Common for all users
    private LocalDateTime lastUpdated;  // Common for all users


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_providers_clients",
            joinColumns = @JoinColumn(name = "provider_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    @JsonIgnoreProperties("providers")
    private List<User> clients;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "clients")
    @JsonIgnoreProperties("clients")
    private List<User> providers;


    @PrePersist
    public void prePersist() {
        registrationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
