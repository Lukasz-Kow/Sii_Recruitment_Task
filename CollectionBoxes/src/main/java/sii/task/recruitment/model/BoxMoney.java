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
public class BoxMoney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Currency must be provided.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "collection_box_id", nullable = false)
    private CollectionBox collectionBox;

    @NotNull(message = "Amount must be provided.")
    @DecimalMin(value = "0.00", message = "Amount can not be negative.")
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
}
