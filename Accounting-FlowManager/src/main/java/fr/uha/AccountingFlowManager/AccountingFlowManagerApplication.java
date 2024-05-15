package fr.uha.AccountingFlowManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class AccountingFlowManagerApplication {

	public static void main(String[] args) {

		SpringApplication.run(AccountingFlowManagerApplication.class, args);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String password [] = {"1234", "Password2", "Password3"};
		for(int i = 0; i < password.length; i++)
			System.out.println(passwordEncoder.encode(password[i]));

	}


}
