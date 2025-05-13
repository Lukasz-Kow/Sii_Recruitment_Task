package sii.task.recruitment.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.dto.AddDonation;
import sii.task.recruitment.model.Donation;
import sii.task.recruitment.service.DonationService;

@RestController
@RequestMapping("/api/donations")
public class DonationController {
    private final DonationService donationService;

    @Autowired
    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    @PostMapping
    public ResponseEntity<Donation> addDonation(@Valid @RequestBody AddDonation request) {
        Donation donation = donationService.addMoneyToTheCollectionBox(request.collectionBoxId(), request.amount(), request.currency());
        return new ResponseEntity<>(donation, HttpStatus.CREATED);
    }
}
