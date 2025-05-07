package sii.task.recruitment.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionBox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @ManyToOne
    @JoinColumn(name = "fundraising_event_id")
    private FundraisingEvent fundraisingEvent;

    @OneToMany(mappedBy = "collectionBox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoxMoney> collectedMoney = new ArrayList<>();
}
