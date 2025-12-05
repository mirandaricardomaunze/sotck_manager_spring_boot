package com.stock.stockmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "suppliers", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "email", "phone", "nuit"})
})
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;            // Nome do fornecedor
    @Column(nullable = false)
    private String email;           // Email do fornecedor
    @Column(nullable = false)
    private String phone;           // Telefone do fornecedor
    @Column(nullable = false)
    private String nuit;            // NUIT do fornecedor
    @Column(nullable = false)
    private String address;         // Endereço
    @Column(nullable = true)
    private String website;         // Website (opcional)
    @Column(nullable = true)
    private String notes;           // Observações adicionais

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private List<Product> products;

}
