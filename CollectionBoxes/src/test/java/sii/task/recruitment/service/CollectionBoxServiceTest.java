package sii.task.recruitment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sii.task.recruitment.dto.CollectionBoxResponse;
import sii.task.recruitment.exception.*;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.FundraisingEventRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionBoxServiceTest {

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    @Mock
    private FundraisingEventRepository fundraisingEventRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private FundraisingEventService fundraisingEventService;

    private CollectionBoxService collectionBoxService;

    @BeforeEach
    void setUp() {
        collectionBoxService = new CollectionBoxService(
                collectionBoxRepository,
                fundraisingEventRepository,
                exchangeRateService,
                fundraisingEventService
        );
    }

    @Test
    void shouldRegisterNewCollectionBox() {
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");

        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox);


        CollectionBox registeredBox = collectionBoxService.registerNewCollectionBox("Box-001");

        Assertions.assertThat(registeredBox).isNotNull();
        Assertions.assertThat(registeredBox.getIdentifier()).isEqualTo("Box-001");

        verify(collectionBoxRepository).save(argThat(box -> box.getIdentifier().equals("Box-001")));
    }

    @Test
    void shouldReturnAllCollectionBoxes() {
        CollectionBox collectionBox1 = new CollectionBox();
        collectionBox1.setIdentifier("Box-001");
        CollectionBox collectionBox2 = new CollectionBox();
        collectionBox2.setIdentifier("Box-002");

        List<CollectionBox> expectedCollectionBoxes = Arrays.asList(collectionBox1, collectionBox2);
        when(collectionBoxRepository.findAll()).thenReturn(expectedCollectionBoxes);

        List<CollectionBox> realCollectionBoxes = collectionBoxService.getAllCollectionBoxes();

        Assertions.assertThat(realCollectionBoxes).containsAll(expectedCollectionBoxes);
        verify(collectionBoxRepository).findAll();
    }

    @Test
    void shouldUnregisterCollectionBox() {
        CollectionBox collectionBox1 = new CollectionBox();
        collectionBox1.setIdentifier("Box-001");

        when(collectionBoxRepository.save(any(CollectionBox.class))).thenReturn(collectionBox1);
        when(collectionBoxRepository.findByIdentifier(collectionBox1.getIdentifier())).thenReturn(Optional.of(collectionBox1));

        CollectionBox registeredBox = collectionBoxService.registerNewCollectionBox("Box-001");

        Assertions.assertThat(registeredBox).isNotNull();

        collectionBoxService.unregisterCollectionBox(registeredBox.getIdentifier());

        verify(collectionBoxRepository).delete(collectionBox1);
    }

    @Test
    void shouldThrowCollectionBoxNotFoundExceptionWhenUnregisteringCollectionBox() {
        String identifier = "Box-001";
        when(collectionBoxRepository.findByIdentifier(identifier)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> collectionBoxService.unregisterCollectionBox(identifier))
                .isInstanceOf(CollectionBoxNotFoundException.class)
                .hasMessageContaining("Collection box with identifier: Box-001, not found.");

        verify(collectionBoxRepository, never()).delete(any());
    }

    @Test
    void shouldAssignEmptyBoxToFundraisingEvent() {
        Long boxId = 1L;
        Long eventId = 10L;

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);

        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(eventId);

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(fundraisingEvent));

        collectionBoxService.assignToFundraisingEvent(boxId, eventId);

        Assertions.assertThat(collectionBox.getFundraisingEvent()).usingRecursiveComparison().isEqualTo(fundraisingEvent);
        verify(collectionBoxRepository).save(collectionBox);
    }

    @Test
    void shouldThrowBoxNotFoundExceptionWhenAssignToFundraisingEvent() {
        Long boxId = 1L;
        Long eventId = 10L;

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> collectionBoxService.assignToFundraisingEvent(boxId, eventId))
                .isInstanceOf(CollectionBoxNotFoundException.class).hasMessageContaining("Collection box with ID: 1, not found.");

        verify(collectionBoxRepository, never()).save(any());
    }

    @Test
    void shouldThrowFundraisingEventNotFoundWhenAssignToFundraisingEvent() {
        Long boxId = 1L;
        Long eventId = 10L;

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> collectionBoxService.assignToFundraisingEvent(boxId, eventId))
                .isInstanceOf(FundraisingEventNotFoundException.class).hasMessageContaining("Fundraising event with ID: 10, not found.");

        verify(collectionBoxRepository, never()).save(any());
    }

    @Test
    void shouldThrowBoxNotEmtpyWhenAssignToFundraisingEvent() {
        Long boxId = 1L;
        Long eventId = 10L;

        Donation donation = new Donation();
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setCollectedMoney(List.of(donation));

        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(eventId);

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        when(fundraisingEventRepository.findById(eventId)).thenReturn(Optional.of(fundraisingEvent));

        Assertions.assertThatThrownBy(() -> collectionBoxService.assignToFundraisingEvent(boxId, eventId))
                .isInstanceOf(CollectionBoxIsNotEmptyException.class)
                .hasMessageContaining("You can only assign empty collection boxes to a fundraising event.");

        verify(collectionBoxRepository, never()).save(any());
    }

    @Test
    void shouldHasCorrectCollectionBoxResponseDto() {
        Long boxId = 1L;
        Long eventId = 10L;

        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(eventId);

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setFundraisingEvent(fundraisingEvent);
        collectionBox.setCollectedMoney(new ArrayList<>());

        CollectionBoxResponse actualBox = collectionBoxService.toDto(collectionBox);

        CollectionBoxResponse expectedBox = new CollectionBoxResponse(1L, "Box-001", true, true);

        Assertions.assertThat(actualBox).usingRecursiveComparison().isEqualTo(expectedBox);
    }

    @Test
    void shouldTransferMoneyToEventsAccount() {
        Long boxId = 1L;

        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(1L);
        fundraisingEvent.setEventCurrency("EUR");

        Donation donation = Donation.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();


        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setFundraisingEvent(fundraisingEvent);
        collectionBox.setCollectedMoney(new ArrayList<>(List.of(donation)));

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        when(exchangeRateService.convert("USD", "EUR", new BigDecimal("100.00"))).thenReturn(new BigDecimal("85.00"));

        collectionBoxService.transferMoneyToEventsAccount(boxId);

        verify(fundraisingEventService).addToBalance(fundraisingEvent, new BigDecimal("85.00"));
        Assertions.assertThat(collectionBox.getCollectedMoney()).isEmpty();
        verify(collectionBoxRepository).save(collectionBox);
    }

    @Test
    void shouldThrowCollectionBoxNotFoundExceptionWhenTransferMoneyToEventsAccount() {
        Long boxId = 1L;

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.empty());
        Assertions.assertThatThrownBy(() -> collectionBoxService.transferMoneyToEventsAccount(boxId))
                .isInstanceOf(CollectionBoxNotFoundException.class).hasMessageContaining("Collection box with ID: 1, not found.");
        verifyNoInteractions(fundraisingEventService, exchangeRateService);
    }

    @Test
    void shouldThrowCollectionBoxIsEmptyExceptionWhenTransferMoneyToEventsAccount() {
        Long boxId = 1L;

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setCollectedMoney(new ArrayList<>());
        collectionBox.setFundraisingEvent(new FundraisingEvent());

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        Assertions.assertThatThrownBy(() -> collectionBoxService.transferMoneyToEventsAccount(boxId))
                .isInstanceOf(CollectionBoxEmptyException.class)
                .hasMessageContaining("Collection box with ID: 1, does not contain any money.");

        verifyNoInteractions(fundraisingEventService, exchangeRateService);
    }

    @Test
    void shouldThrowBoxNotAssignedToEventExceptionWhenTransferMoneyToEventsAccount() {
        Long boxId = 1L;

        Donation donation = Donation.builder()
                .amount(new BigDecimal("100.00"))
                .currency("USD")
                .build();

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setFundraisingEvent(null);
        collectionBox.setCollectedMoney(List.of(donation));

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        Assertions.assertThatThrownBy(() -> collectionBoxService.transferMoneyToEventsAccount(boxId))
                .isInstanceOf(CollectionBoxNotAssignedException.class)
                .hasMessageContaining("Cannot transfer money, collection box with ID 1 is not assigned to any event.");

        verifyNoInteractions(fundraisingEventService, exchangeRateService);
    }

    @Test
    void shouldSkipExchangeWhenCurrenciesMatchWhenTransferMoneyToEventsAccount() {
        Long boxId = 1L;
        String currency = "EUR";

        FundraisingEvent fundraisingEvent = new FundraisingEvent();
        fundraisingEvent.setId(10L);
        fundraisingEvent.setEventCurrency(currency);

        Donation donation = Donation.builder()
                .amount(new BigDecimal("200.00"))
                .currency(currency)
                .build();

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-001");
        collectionBox.setId(boxId);
        collectionBox.setFundraisingEvent(fundraisingEvent);
        collectionBox.setCollectedMoney(new ArrayList<>(List.of(donation)));

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.of(collectionBox));
        collectionBoxService.transferMoneyToEventsAccount(boxId);

        verify(fundraisingEventService).addToBalance(fundraisingEvent, new BigDecimal("200.00"));
        verify(exchangeRateService, never()).convert(any(), any(), any());

        Assertions.assertThat(collectionBox.getCollectedMoney()).isEmpty();
        verify(collectionBoxRepository).save(collectionBox);
    }


}