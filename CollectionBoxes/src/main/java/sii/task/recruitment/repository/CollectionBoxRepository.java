package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.task.recruitment.model.CollectionBox;

import java.util.Optional;

public interface CollectionBoxRepository extends JpaRepository<CollectionBox, Long> {
    boolean existsByIdentifier(String identifier);

    Optional<CollectionBox> findByIdentifier(String identifier);
}
