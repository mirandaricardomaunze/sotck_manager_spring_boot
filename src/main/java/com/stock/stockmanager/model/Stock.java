package com.stock.stockmanager.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Produto associado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    // Armazém associado
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    // Quantidade real no armazém
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private Integer reservedQuantity = 0;



    // Controle de concorrência otimista
    @Version
    private Long version;

    // ================= MÉTODOS AUXILIARES =================

    /**
     * Quantidade disponível para novos pedidos.
     */
    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    /**
     * Reserva uma quantidade de estoque para um pedido.
     */
    public void reserveStock(int amount) {
        if (amount <= 0) return;
        if (amount > getAvailableQuantity()) {
            throw new IllegalArgumentException("Estoque insuficiente disponível para reserva");
        }
        reservedQuantity += amount;
    }

    /**
     * Libera uma quantidade reservada (ex: pedido cancelado).
     */
    public void releaseStock(int amount) {
        if (amount <= 0) return;
        reservedQuantity -= amount;
        if (reservedQuantity < 0) reservedQuantity = 0;
    }

    /**
     * Deduz quantidade do estoque real (ex: faturamento).
     * A reserva correspondente também é reduzida.
     */
    public void deductStock(int amount) {
        if (amount <= 0) return;
        if (amount > quantity) {
            throw new IllegalArgumentException("Estoque insuficiente para faturamento");
        }
        quantity -= amount;

        // Reduz da reserva, se houver
        if (reservedQuantity >= amount) {
            reservedQuantity -= amount;
        } else {
            reservedQuantity = 0;
        }
    }

}
