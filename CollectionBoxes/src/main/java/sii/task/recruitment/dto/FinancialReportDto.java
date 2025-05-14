package sii.task.recruitment.dto;

import java.math.BigDecimal;

public record FinancialReportDto(String eventName, BigDecimal accountBalance, String eventCurrency) {
}
