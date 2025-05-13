package sii.task.recruitment.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import sii.task.recruitment.model.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class DonationRepositoryTest {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private CollectionBoxRepository collectionBoxRepository;

    @Test
    void saveDonation() {
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-Test");

        CollectionBox savedBox = collectionBoxRepository.save(collectionBox);

        Donation donation = Donation.builder()
                .amount(new BigDecimal("55.00"))
                .currency("EUR")
                .collectionBox(savedBox)
                .build();

        Donation savedDonation = donationRepository.save(donation);
        assertNotNull(savedDonation.getId());
        assertEquals(new BigDecimal("55.00"), savedDonation.getAmount());
        assertEquals("EUR", savedDonation.getCurrency());
        assertEquals("Box-Test", savedDonation.getCollectionBox().getIdentifier());
    }

    @Test
    void findDonationById() {
        CollectionBox collectionBox = new CollectionBox();
        collectionBox.setIdentifier("Box-Test2");

        CollectionBox savedBox = collectionBoxRepository.save(collectionBox);

        Donation donation = Donation.builder()
                .amount(new BigDecimal("215.00"))
                .currency("USD")
                .collectionBox(savedBox)
                .build();

        Donation savedDonation = donationRepository.save(donation);

        Optional<Donation> foundDonation = donationRepository.findById(savedDonation.getId());

        assertTrue(foundDonation.isPresent());
        assertEquals(savedDonation.getId(), foundDonation.get().getId());
        assertEquals(savedDonation.getAmount(), foundDonation.get().getAmount());
        assertEquals(savedDonation.getCurrency(), foundDonation.get().getCurrency());
    }
}