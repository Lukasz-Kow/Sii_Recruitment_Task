package sii.task.recruitment.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import sii.task.recruitment.exception.CollectionBoxNotFoundException;
import sii.task.recruitment.model.*;
import sii.task.recruitment.repository.CollectionBoxRepository;
import sii.task.recruitment.repository.DonationRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationService donationService;

    @Mock
    private CollectionBoxRepository collectionBoxRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldAddDonation() {
        Long collectionBoxId = 1L;
        BigDecimal amount = new BigDecimal("100.00");
        Currency currency = Currency.EUR;

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

        when(collectionBoxRepository.findById(boxId)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            donationService.addMoneyToTheCollectionBox(boxId, new BigDecimal("100.00"), Currency.EUR);
        }).isInstanceOf(CollectionBoxNotFoundException.class).hasMessageContaining("Collection box with ID: 2, not found.");
        verifyNoInteractions(donationRepository);
    }

}
