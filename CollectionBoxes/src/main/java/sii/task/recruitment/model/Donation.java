package sii.task.recruitment.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Currency must be provided.")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be valid 3 letter ISO code.")
    private String currency;

    @ManyToOne
    @JoinColumn(name = "collection_box_id", nullable = false)
    @JsonBackReference
    private CollectionBox collectionBox;

    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;
}
