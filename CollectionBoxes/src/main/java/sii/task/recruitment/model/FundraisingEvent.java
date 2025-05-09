package sii.task.recruitment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FundraisingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "The name of the event is mandatory.")
    @Column(nullable = false)
    private String eventName;

    @NotNull(message = "Currency must be specified.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency eventCurrency;

    @NotNull(message = "Account balance must be specified.")
    @DecimalMin(value = "0.00", message = "Account balance can not be negative.")
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal accountBalance = BigDecimal.ZERO;
}
