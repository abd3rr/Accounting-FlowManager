package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.enums.RoleName;
import fr.uha.AccountingFlowManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import fr.uha.AccountingFlowManager.model.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    List<Role> findAll();
    Optional<Role> findById(long id);

    Optional<Role> findByName(RoleName roleName);
}
