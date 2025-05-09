package sii.task.recruitment.dto;

import sii.task.recruitment.model.Currency;

import java.math.BigDecimal;

public record AddDonation(BigDecimal amount, Currency currency) {

}
