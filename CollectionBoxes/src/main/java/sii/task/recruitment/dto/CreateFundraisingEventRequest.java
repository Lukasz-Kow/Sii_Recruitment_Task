package sii.task.recruitment.dto;

import sii.task.recruitment.model.Currency;

public record CreateFundraisingEventRequest(String eventName, Currency currency) {
}
