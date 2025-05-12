package sii.task.recruitment.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.CurrencyConverter;
import sii.task.recruitment.dto.CollectionBoxResponse;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.FundraisingEventRepository;

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
                .orElseThrow(() -> new CollectionBoxNotFoundException(String.format("Collection box with identifier: %s, not found.", identifier)));
        collectionBoxRepository.delete(box);
    }

    public void assignToFundraisingEvent(Long collectionBoxId, Long fundraisingEventId) {
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException(String.format("Collection box with ID: %d, not found.", collectionBoxId)));
        FundraisingEvent event = fundraisingEventRepository.findById(fundraisingEventId)
                .orElseThrow(() -> new FundraisingEventNotFoundException(String.format("Fundraising event  with ID: %d, not found.", fundraisingEventId)));

        if (!box.getCollectedMoney().isEmpty()) {
            throw new CollectionBoxIsNotEmptyException("You can only assign empty collection boxes to a fundraising event.");
        }

        box.setFundraisingEvent(event);
        collectionBoxRepository.save(box);
    }

    @Transactional
    public void transferMoneyToEventsAccount(Long collectionBoxId) {
        CollectionBox box = collectionBoxRepository.findById(collectionBoxId)
                .orElseThrow(() -> new CollectionBoxNotFoundException(String.format("Collection box with ID: %d, not found.", collectionBoxId)));

        if (box.getCollectedMoney().isEmpty()) {
            throw new CollectionBoxEmptyException(String.format("Collection box with ID: %d, does not contain any money.", collectionBoxId));
        }

        FundraisingEvent boxesEvent = box.getFundraisingEvent();

        if (boxesEvent == null) {
            throw new CollectionBoxNotAssignedException(String.format("Cannot transfer money, collection box with ID %d is not assigned to any event.", collectionBoxId));
        }

        Currency targetCurrency = boxesEvent.getEventCurrency();
        BigDecimal totalMoney = box.getCollectedMoney().stream().map(donation -> donation.getCurrency().equals(targetCurrency)
                        ? donation.getAmount()
                        : currencyConverter.convertCurrency(donation.getAmount(), donation.getCurrency(), targetCurrency))
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        fundraisingEventService.addToBalance(boxesEvent, totalMoney);
        box.getCollectedMoney().clear();
        collectionBoxRepository.save(box);
    }

    public CollectionBoxResponse toDto(CollectionBox collectionBox) {
        boolean isAssigned = collectionBox.getFundraisingEvent() != null;
        boolean isEmpty = collectionBox.getFundraisingEvent() == null || collectionBox.getCollectedMoney().isEmpty();
        return new CollectionBoxResponse(collectionBox.getId(), collectionBox.getIdentifier(), isAssigned, isEmpty);
    }


}
