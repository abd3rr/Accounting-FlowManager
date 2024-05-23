package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<ProductCatalog, Long> {
    List<ProductCatalog> findAll();

    List<ProductCatalog> findByName(String name);

    List<ProductCatalog> findByProvider_Id(Long providerId);

}