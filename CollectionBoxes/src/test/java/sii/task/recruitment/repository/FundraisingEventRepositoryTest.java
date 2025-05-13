package sii.task.recruitment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sii.task.recruitment.model.FundraisingEvent;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FundraisingEventRepositoryTest {

    @Autowired
    private FundraisingEventRepository fundraisingEventRepository;

    @Test
    void testSaveAndFindFundraisingEvent() {
        FundraisingEvent event = FundraisingEvent.builder()
                .eventName("Event1")
                .eventCurrency("EUR")
                .accountBalance(BigDecimal.TEN)
                .build();

        FundraisingEvent savedEvent = fundraisingEventRepository.save(event);

        Optional<FundraisingEvent> found = fundraisingEventRepository.findByEventName(savedEvent.getEventName());

        assertTrue(found.isPresent());
        assertEquals(savedEvent.getEventName(), found.get().getEventName());
        assertEquals(savedEvent.getEventCurrency(), found.get().getEventCurrency());
        assertEquals(savedEvent.getAccountBalance(), found.get().getAccountBalance());

    }
}