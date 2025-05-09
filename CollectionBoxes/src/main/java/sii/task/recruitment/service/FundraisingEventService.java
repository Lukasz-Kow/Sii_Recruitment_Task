package sii.task.recruitment.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.FundraisingEvent;
import sii.task.recruitment.repository.FundraisingEventRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service

public class FundraisingEventService {

    private final FundraisingEventRepository fundraisingEventRepository;


    @Autowired
    public FundraisingEventService(FundraisingEventRepository fundraisingEventRepository) {
        this.fundraisingEventRepository = fundraisingEventRepository;
    }

    @Transactional
    public FundraisingEvent createFundraisingEvent(String eventName, Currency currency) {
        FundraisingEvent event = FundraisingEvent.builder()
                .eventName(eventName)
                .eventCurrency(currency)
                .accountBalance(BigDecimal.ZERO)
                .build();

        return fundraisingEventRepository.save(event);
    }

    public Optional<FundraisingEvent> getFundraisingEventByName(String nameEvent) {
        return fundraisingEventRepository.findByEventName(nameEvent);
    }

    public List<FundraisingEvent> getAllFundraisingEvents() {
        return fundraisingEventRepository.findAll();
    }

    @Transactional
    public void addToBalance(FundraisingEvent event, BigDecimal amount) {
        event.setAccountBalance(event.getAccountBalance().add(amount));
        fundraisingEventRepository.save(event);
    }

}
