package sii.task.recruitment.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.exception.CollectionBoxNotFoundException;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.DonationRepository;

import java.math.BigDecimal;

@Service
public class DonationService {
    private final DonationRepository donationRepository;
    private final CollectionBoxRepository collectionBoxRepository;

    @Autowired
    public DonationService(DonationRepository donationRepository, CollectionBoxRepository collectionBoxRepository) {
        this.donationRepository = donationRepository;
        this.collectionBoxRepository = collectionBoxRepository;
    }

    @Transactional
    public Donation addMoneyToTheCollectionBox(Long collectionBoxId, BigDecimal money, String currency) {
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException(String.format("Collection box with ID: %d, not found.", collectionBoxId)));

        Donation donation = Donation.builder()
                .amount(money)
                .currency(currency)
                .collectionBox(box).build();

        return donationRepository.save(donation);
    }
}
