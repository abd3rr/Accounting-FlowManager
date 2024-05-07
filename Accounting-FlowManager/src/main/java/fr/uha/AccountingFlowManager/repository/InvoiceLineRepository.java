package fr.uha.AccountingFlowManager.repository;
import fr.uha.AccountingFlowManager.model.InvoiceLine;

import fr.uha.AccountingFlowManager.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    List<InvoiceLine> findAll();
    Optional<InvoiceLine> findById(long id);

}
