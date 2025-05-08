package sii.task.recruitment.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.*;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository collectionBoxRepository;
    private final FundraisingEventRepository fundraisingEventRepository;
    private final DonationRepository donationRepository;


    @Autowired
    public CollectionBoxService(CollectionBoxRepository collectionBoxRepository,
                                FundraisingEventRepository fundraisingEventRepository,
                                DonationRepository donationRepository) {
        this.collectionBoxRepository = collectionBoxRepository;
        this.fundraisingEventRepository = fundraisingEventRepository;
        this.donationRepository = donationRepository;
    }

    @Transactional
    public CollectionBox registerNewCollectionBox(String identifier) {
        if (collectionBoxRepository.existsByIdentifier(identifier)) {
            throw new CollectionBoxExistsException("identifier");
        }
        CollectionBox newBox = new CollectionBox();
        newBox.setIdentifier(identifier);
        return collectionBoxRepository.save(newBox);
    }

    public List<CollectionBox> getAllCollectionBoxes() {
        return collectionBoxRepository.findAll();
    }

    @Transactional
    public void unregisterCollectionBox(String identifier) {
        CollectionBox box = collectionBoxRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new CollectionBoxNotFoundException("collectionBoxId")); //TODO: Add handler for exceptions

        collectionBoxRepository.delete(box);
    }

    @Transactional
    public boolean assignToFundraisingEvent(Long collectionBoxId, Long fundraisingEventId) {
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException("collectionBoxId")); //TODO: Add handler for exceptions
        FundraisingEvent event = fundraisingEventRepository.findById(fundraisingEventId)
                .orElseThrow(() -> new FundraisingEventNotFoundException("fundraisingEventId")); //TODO: Add handler for exceptions

        if (!box.getCollectedMoney().isEmpty()) {
            throw new CollectionBoxIsNotEmptyException("");
        }

        box.setFundraisingEvent(event);
        collectionBoxRepository.save(box);
        return true;
    }

    @Transactional
    public void addMoneyToTheCollectionBox(Long collectionBoxId, BigDecimal money, Currency currency) throws IllegalAmountException {
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalAmountException("Money > 0");
        }
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException("collectionBoxId")); //TODO: Add handler for exceptions

        Donation donation = Donation.builder()
                .amount(money)
                .currency(currency)
                .collectionBox(box).build();
        donationRepository.save(donation);
    }



}
