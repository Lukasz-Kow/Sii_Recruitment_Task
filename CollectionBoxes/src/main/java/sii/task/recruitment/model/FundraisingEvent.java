package sii.task.recruitment.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundraisingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String eventName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency eventCurrency;

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal accountBalance;
}
