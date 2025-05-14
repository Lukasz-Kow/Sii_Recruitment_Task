package sii.task.recruitment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import sii.task.recruitment.exception.CollectionBoxNotFoundException;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.DonationRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    private DonationService donationService;

    @BeforeEach
    void setUp() {
        donationService = new DonationService(donationRepository, collectionBoxRepository);
    }

    @Test
    void shouldAddDonation() {
        Long collectionBoxId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        String currency = "EUR";

        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setId(collectionBoxId);
        collectionBox.setIdentifier("Box-001");

        Donation expectedDonation = Donation.builder()
                .collectionBox(collectionBox)
                .amount(amount)
                .currency(currency)
                .build();

        when(collectionBoxRepository.findById(collectionBoxId)).thenReturn(Optional.of(collectionBox));
        when(donationRepository.save(any(Donation.class))).thenReturn(expectedDonation);

        Donation saved = donationService.addMoneyToTheCollectionBox(collectionBoxId, amount, currency);

        Assertions.assertThat(saved).usingRecursiveComparison().isEqualTo(expectedDonation);

        verify(donationRepository).save(argThat(d -> d.getCollectionBox().equals(collectionBox) && d.getAmount()
                .equals(amount) && d.getCurrency().equals(currency)));
    }

    @Test
    void shouldThrowExceptionWhenCollectionBoxNotFound() {
        Long boxId = 2L;
        BigDecimal amount = new BigDecimal("100.00");
        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.empty());
        String currency = "EUR";

        Assertions.assertThatThrownBy(() -> {
            donationService.addMoneyToTheCollectionBox(boxId, amount, currency);
        }).isInstanceOf(CollectionBoxNotFoundException.class).hasMessageContaining("Collection box with ID: 2, not found.");
        verifyNoInteractions(donationRepository);
    }

}
