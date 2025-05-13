package sii.task.recruitment.dto;

import sii.task.recruitment.model.Currency;

import java.math.BigDecimal;

public record FinancialReportDto(String eventName, BigDecimal accountBalance, Currency eventCurrency) {
}
