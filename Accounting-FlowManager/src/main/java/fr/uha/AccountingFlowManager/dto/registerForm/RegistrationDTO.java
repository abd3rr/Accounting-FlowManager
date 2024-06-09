package fr.uha.AccountingFlowManager.dto.registerForm;

import lombok.Data;

import java.util.List;

@Data
public class RegistrationDTO {
    private String fullName;
    private String email;
    private String password;
    private String confirmPassword;
    private String address;
    private String country;
    private String phoneNumber;
    private List<Long> providers;
}
