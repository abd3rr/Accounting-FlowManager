package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.ProductCatalog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductCatalog, Long> {
    List<ProductCatalog> findAll();

    Optional<ProductCatalog> findById(long id);

    Optional<ProductCatalog> findByNameAndProvider_Id(String name, Long providerId);

    List<ProductCatalog> findByName(String name);

    List<ProductCatalog> findByProvider_Id(Long providerId);
    List<ProductCatalog> findByNameContainingIgnoreCase(String name);


}