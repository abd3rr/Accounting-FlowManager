package fr.uha.AccountingFlowManager.repository;
import fr.uha.AccountingFlowManager.model.Account;
import fr.uha.AccountingFlowManager.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAll();
    Optional<Account> findById(long id);

}
