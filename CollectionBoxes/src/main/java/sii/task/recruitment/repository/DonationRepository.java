package sii.task.recruitment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sii.task.recruitment.model.Donation;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
