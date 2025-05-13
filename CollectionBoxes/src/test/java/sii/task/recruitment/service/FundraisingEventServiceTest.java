package sii.task.recruitment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.FundraisingEvent;
import sii.task.recruitment.repository.FundraisingEventRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FundraisingEventServiceTest {

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;

    @InjectMocks
    private FundraisingEventService fundraisingEventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateFundraisingEvent() {
        FundraisingEvent fundraisingEvent = FundraisingEvent.builder()
                .eventName("Event_1")
                .eventCurrency(Currency.GBP)
                .accountBalance(BigDecimal.ZERO)
                .build();

        when(fundraisingEventRepository.save(any(FundraisingEvent.class))).thenReturn(fundraisingEvent);

        FundraisingEvent createdEvent = fundraisingEventService.createFundraisingEvent("Event_1", Currency.GBP);

        Assertions.assertThat(createdEvent).usingRecursiveComparison().isEqualTo(fundraisingEvent);

        verify(fundraisingEventRepository).save(any(FundraisingEvent.class));
    }

    @Test
    void shouldGetFundraisingEventByName() {
        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setEventName("Event_1");

        when(fundraisingEventRepository.findByEventName("Event_1")).thenReturn(Optional.of(fundraisingEvent));

        Optional<FundraisingEvent> result = fundraisingEventService.getFundraisingEventByName("Event_1");

        Assertions.assertThat(result).isEqualTo(Optional.of(fundraisingEvent));

        verify(fundraisingEventRepository).findByEventName("Event_1");
    }

    @Test
    void shouldGetAllFundraisingEvents() {
        FundraisingEvent fundraisingEvent1 = new FundraisingEvent();
        fundraisingEvent1.setEventName("Event_1");
        FundraisingEvent fundraisingEvent2 = new FundraisingEvent();
        fundraisingEvent2.setEventName("Event_2");

        when(fundraisingEventRepository.findAll()).thenReturn(Arrays.asList(fundraisingEvent1, fundraisingEvent2));

        List<FundraisingEvent> events = fundraisingEventService.getAllFundraisingEvents();

        Assertions.assertThat(events).hasSize(2).extracting(FundraisingEvent::getEventName).containsExactlyInAnyOrder("Event_1", "Event_2");

        verify(fundraisingEventRepository).findAll();
    }

    @Test
    void shouldAddToFundraisingEventBalance() {
        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setAccountBalance(new BigDecimal("100.00"));

        BigDecimal amountToAdd = new BigDecimal("100.00");
        fundraisingEventService.addToBalance(fundraisingEvent, amountToAdd);

        Assertions.assertThat(fundraisingEvent.getAccountBalance()).isEqualTo(new BigDecimal("200.00"));
        verify(fundraisingEventRepository).save(fundraisingEvent);
    }


}
