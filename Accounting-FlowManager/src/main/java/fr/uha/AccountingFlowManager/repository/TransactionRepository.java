package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.Transaction;
import fr.uha.AccountingFlowManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAll();

    Optional<Transaction> findById(long id);
    List<Transaction> findByProvider(User provider);
}
