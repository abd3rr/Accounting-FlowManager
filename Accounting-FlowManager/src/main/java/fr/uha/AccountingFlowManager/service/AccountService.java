package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.enums.AccountType;
import fr.uha.AccountingFlowManager.enums.TransactionType;
import fr.uha.AccountingFlowManager.model.Account;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account findOrCreateAccountByProviderAndType(User provider, AccountType accountType) {
        return accountRepository.findByProviderAndAccountType(provider, accountType)
                .orElseGet(() -> createAccount(provider, accountType));
    }

    private Account createAccount(User provider, AccountType accountType) {
        if (accountRepository.existsByProviderAndAccountType(provider, accountType)) {
            throw new IllegalStateException("Account of type " + accountType + " already exists for this provider.");
        }
        Account newAccount = new Account();
        newAccount.setProvider(provider);
        newAccount.setAccountType(accountType);
        newAccount.setBalance(0.0);  // Initial balance
        return accountRepository.save(newAccount);
    }

    public void adjustAccountBalance(Account account, double amount, TransactionType transactionType) {
        if (transactionType == TransactionType.DEBIT) {
            account.setBalance(account.getBalance() + amount); // Increase for CREDIT
        }
        accountRepository.save(account);
    }
}
