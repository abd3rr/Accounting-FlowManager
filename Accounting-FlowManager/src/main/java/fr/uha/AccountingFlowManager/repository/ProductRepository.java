package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductCatalog, Long> {

    List<ProductCatalog> findByName(String name);
}