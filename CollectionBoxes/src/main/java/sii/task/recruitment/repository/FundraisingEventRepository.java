package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.task.recruitment.model.FundraisingEvent;

public interface FundraisingEventRepository extends JpaRepository<FundraisingEvent, Long> {
}
