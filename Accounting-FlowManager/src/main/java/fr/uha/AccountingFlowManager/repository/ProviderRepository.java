package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.Provider;
import fr.uha.AccountingFlowManager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findAll();
    Optional<Provider> findById(long id);
}
