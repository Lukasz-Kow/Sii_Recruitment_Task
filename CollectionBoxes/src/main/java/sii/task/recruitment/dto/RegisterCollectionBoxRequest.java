package sii.task.recruitment.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterCollectionBoxRequest(

        @NotBlank(message = "Box identifier is mandatory.")
        String identifier
) {
}
