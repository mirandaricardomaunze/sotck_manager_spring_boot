package com.stock.stockmanager.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_sequence")
@Getter
@Setter
public class OrderSequence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nextValue;
    @Version
    private Long version;
}
