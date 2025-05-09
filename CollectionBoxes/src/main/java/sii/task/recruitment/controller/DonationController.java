package sii.task.recruitment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sii.task.recruitment.dto.AddDonation;
import sii.task.recruitment.exception.IllegalAmountException;
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

    @PostMapping("/{boxId}")
    public ResponseEntity<Donation> addDonation(@PathVariable Long boxId, @RequestBody AddDonation request) throws IllegalAmountException {
        Donation donation = donationService.addMoneyToTheCollectionBox(boxId, request.amount(), request.currency());
        return new ResponseEntity<>(donation, HttpStatus.CREATED);
    }
}
