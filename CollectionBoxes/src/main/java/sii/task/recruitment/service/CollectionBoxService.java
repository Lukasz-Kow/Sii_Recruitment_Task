package sii.task.recruitment.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.CurrencyConverter;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.*;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CollectionBoxService {
    private final CollectionBoxRepository collectionBoxRepository;
    private final FundraisingEventRepository fundraisingEventRepository;
    private final CurrencyConverter currencyConverter;
    private final FundraisingEventService fundraisingEventService;

    @Autowired
    public CollectionBoxService(CollectionBoxRepository collectionBoxRepository,
                                FundraisingEventRepository fundraisingEventRepository,
                                CurrencyConverter currencyConverter,
                                FundraisingEventService fundraisingEventService) {
        this.collectionBoxRepository = collectionBoxRepository;
        this.fundraisingEventRepository = fundraisingEventRepository;
        this.currencyConverter = currencyConverter;
        this.fundraisingEventService = fundraisingEventService;
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
    public boolean transferMoneyToEventsAccount(Long collectionBoxId) {
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException("collectionBoxId")); //TODO: Add handler for exceptions

        if (box.getCollectedMoney().isEmpty()) {
            return false;
        }

        FundraisingEvent boxesEvent = box.getFundraisingEvent();
        Currency targetCurrency = boxesEvent.getEventCurrency();
        BigDecimal totalMoney = BigDecimal.ZERO;

        for (Donation donation : box.getCollectedMoney()) {
            BigDecimal convertedMoney;
            if (donation.getCurrency().equals(targetCurrency)) {
                convertedMoney = donation.getAmount();
            } else {
                convertedMoney = currencyConverter.convertCurrency(donation.getAmount(), donation.getCurrency(), targetCurrency);
            }
            totalMoney = totalMoney.add(convertedMoney);
        }
        fundraisingEventService.addToBalance(boxesEvent, totalMoney);
        box.getCollectedMoney().clear();
        collectionBoxRepository.save(box);
        return true;
    }


}
