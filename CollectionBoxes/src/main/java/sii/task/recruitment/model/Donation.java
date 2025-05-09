package sii.task.recruitment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Builder
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Currency must be provided.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "collection_box_id", nullable = false)
    @JsonBackReference
    private CollectionBox collectionBox;

    @NotNull(message = "Amount must be provided.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
}
