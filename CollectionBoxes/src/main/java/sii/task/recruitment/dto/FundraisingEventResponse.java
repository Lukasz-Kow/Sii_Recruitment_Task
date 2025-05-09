package sii.task.recruitment.dto;

import sii.task.recruitment.model.Currency;

import java.math.BigDecimal;

public record FundraisingEventResponse(String eventName, Currency eventCurrency, BigDecimal eventAmount) {
}
