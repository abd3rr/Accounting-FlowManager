package fr.uha.AccountingFlowManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

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

    private String businessName; // In case the User is a Business not a Person

    @Email
    private String email;

    private String passwordHash;

    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")
    private String phoneNumber;

    private String address;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Invoice> invoices;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_providers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "provider_id")
    )
    private List<Provider> providers;


}
