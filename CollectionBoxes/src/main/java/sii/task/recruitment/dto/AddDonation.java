package sii.task.recruitment.dto;

import jakarta.validation.constraints.*;
import sii.task.recruitment.validator.ValidCurrency;

import java.math.BigDecimal;

public record AddDonation(
        @NotNull(message = "CollectionBox ID must be provided.")
        Long collectionBoxId,

        @NotNull(message = "Amount must be provided.")
        BigDecimal amount,

        @NotBlank(message = "Currency must be provided.")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter ISO code.")
        @ValidCurrency
        String currency) {

}
