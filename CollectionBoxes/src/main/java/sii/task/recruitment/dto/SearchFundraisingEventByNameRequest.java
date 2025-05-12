package sii.task.recruitment.dto;

import jakarta.validation.constraints.NotBlank;

public record SearchFundraisingEventByNameRequest(

        @NotBlank(message = "Event name is required.")
        String eventName
) {
}
