package sii.task.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import sii.task.recruitment.model.Currency;

public record CreateFundraisingEventRequest(

        @NotBlank(message = "Event name is mandatory.")
        String eventName,

        @NotBlank(message = "Currency must be specified.")
        Currency currency
) {
}
