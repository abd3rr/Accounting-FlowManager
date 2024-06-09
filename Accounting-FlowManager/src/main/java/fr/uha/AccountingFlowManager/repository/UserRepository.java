package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.Role;
import fr.uha.AccountingFlowManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByFullName(String fullName);

    List<User> findUsersByRole(Role role);

    // Use Iterable<Long> instead of List<Long>
    List<User> findAllById(Iterable<Long> ids);

    @Query("SELECT c FROM User p JOIN p.clients c WHERE p.id = :providerId")
    List<User> findClientsByProviderId(@Param("providerId") Long providerId);
}
