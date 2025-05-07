package sii.task.recruitment.model;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "collection_box_id", nullable = false)
    private CollectionBox collectionBox;

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal amount;
}
