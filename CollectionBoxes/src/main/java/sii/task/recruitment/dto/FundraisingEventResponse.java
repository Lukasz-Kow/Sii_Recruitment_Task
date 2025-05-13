package sii.task.recruitment.dto;


import java.math.BigDecimal;

public record FundraisingEventResponse(String eventName, String eventCurrency, BigDecimal eventAmount) {
}
