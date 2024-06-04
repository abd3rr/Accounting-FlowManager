package fr.uha.AccountingFlowManager.repository;

import fr.uha.AccountingFlowManager.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAll();

    Optional<File> findById(long id);
}
