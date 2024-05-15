package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    List<User> findAll();

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

}
