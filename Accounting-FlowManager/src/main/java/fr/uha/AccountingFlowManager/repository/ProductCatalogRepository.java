package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCatalogRepository extends JpaRepository<ProductCatalog, Long> {
    List<ProductCatalog> findAll();
    Optional<ProductCatalog> findById(long id);
}
