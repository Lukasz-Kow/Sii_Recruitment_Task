package sii.task.recruitment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Source currency must be provided.")
    @Column(nullable = false)
    private String sourceCurrency;

    @NotNull(message = "Target currency must be provided.")
    @Column(nullable = false)
    private String targetCurrency;

    @NotNull(message = "Exchange rate must be provided.")
    @DecimalMin(value = "0.0001", message = "Exchange rate must be greater than 0.0001.")
    @Column(nullable = false, precision = 11, scale = 6)
    private BigDecimal rate;
}
