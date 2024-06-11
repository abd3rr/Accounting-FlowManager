package fr.uha.AccountingFlowManager.service;

import fr.uha.AccountingFlowManager.enums.AccountType;
import fr.uha.AccountingFlowManager.enums.TransactionType;
import fr.uha.AccountingFlowManager.model.Account;
import fr.uha.AccountingFlowManager.model.Invoice;
import fr.uha.AccountingFlowManager.model.Transaction;
import fr.uha.AccountingFlowManager.model.User;
import fr.uha.AccountingFlowManager.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.LocalDateTime;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional
    public Transaction createTransaction(double amount, User provider, Invoice invoice, TransactionType transactionType) {
        Account account = accountService.findOrCreateAccountByProviderAndType(provider, AccountType.BALANCE);
        accountService.adjustAccountBalance(account, amount, transactionType); // Use AccountService for consistency

        Transaction transaction = new Transaction();
        transaction.setDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setDescription("Related to invoice " + invoice.getId());
        transaction.setAccount(account);
        transaction.setProvider(provider);
        transaction.setInvoice(invoice);
        return transactionRepository.save(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsForProvider(User provider) {
        // Validate that the provider is not null
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }

        return transactionRepository.findByProvider(provider);
    }
}




