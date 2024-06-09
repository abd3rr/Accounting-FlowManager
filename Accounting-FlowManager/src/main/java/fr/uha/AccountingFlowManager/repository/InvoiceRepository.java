package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findAll();
    List<Invoice> findByProviderId(Long currentUserId);
    List<Invoice> findByCustomerId(Long currentUserId);

    Optional<Invoice> findById(long id);

}
