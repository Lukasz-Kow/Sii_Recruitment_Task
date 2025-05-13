package sii.task.recruitment.dto;

import jakarta.validation.constraints.NotNull;
import sii.task.recruitment.model.Currency;

import java.math.BigDecimal;

public record AddDonation(
        @NotNull(message = "CollectionBox ID must be provided.")
        Long collectionBoxId,

        @NotNull(message = "Amount must be provided.")
        BigDecimal amount,

        @NotNull(message = "Currency must be provided.")
        Currency currency) {

}
