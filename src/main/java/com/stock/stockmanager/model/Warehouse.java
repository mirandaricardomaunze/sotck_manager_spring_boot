package com.stock.stockmanager.model;

import com.stock.stockmanager.enums.WarehouseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "email", "phone"})
})
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location; // e.g., "City, State, Country"

    @Column(nullable = true)
    private String description; // Optional description of the warehouse

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String email; // melhor usar letra minúscula

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String manager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarehouseStatus status; // enum ACTIVE / INACTIVE

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // NOVO CAMPO: armazém principal
    @Column(nullable = false)
    private boolean principal = false;
}
