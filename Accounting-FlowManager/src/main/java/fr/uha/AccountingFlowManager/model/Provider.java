package fr.uha.AccountingFlowManager.model;

import fr.uha.AccountingFlowManager.enums.Country;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table(name = "providers")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String companyName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User contactPerson;

    @Email
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{3}[)]?[-\\s.]?[0-9]{3}[-\\s.]?[0-9]{4,6}$")
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    private Country country;

    @Pattern(regexp = "https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()!@:%_+.~#?&/=]*)")
    private String websiteUrl;

    private LocalDateTime registrationDate;

    private LocalDateTime lastUpdated;

    @ManyToMany(mappedBy = "providers")
    private List<User> clients;

    @PrePersist
    public void setDateAdded() {
        this.registrationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }

}
