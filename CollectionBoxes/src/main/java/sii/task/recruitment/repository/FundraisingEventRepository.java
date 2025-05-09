package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.task.recruitment.model.FundraisingEvent;

import java.util.Optional;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
    Optional<FundraisingEvent> findByEventName(String eventName);
}
