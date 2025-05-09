package sii.task.recruitment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.model.Currency;
import sii.task.recruitment.model.Donation;
import sii.task.recruitment.service.DonationService;

import java.math.BigDecimal;

@RestController("/api/donation")
public class DonationController {
    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping("/{boxId}")
    public ResponseEntity<Donation> addDonation(@PathVariable Long boxId, @RequestBody BigDecimal amount, Currency currency) {
        Donation donation = donationService.addMoneyToTheCollectionBox(boxId, amount, currency);
        return new ResponseEntity<>(donation, HttpStatus.CREATED);
    }
}
