package com.stock.stockmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sku"}),
        @UniqueConstraint(columnNames = {"barcode"}),
        @UniqueConstraint(columnNames = {"referenceNumber"})
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===================== DADOS BÁSICOS =====================
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false, unique = true)
    private String barcode;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private Integer boxes; // itens por caixa

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false)
    private Integer quantityInStock;

    @Column(nullable = false)
    private Integer minimumStockLevel;

    @Column(nullable = false)
    private String unitOfMeasure;

    // ===================== CAMPOS EMPRESARIAIS =====================
    @Column(precision = 15, scale = 2)
    private BigDecimal lastPurchasePrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal averageCost;

    private Integer maximumStockLevel;

    private Integer reorderPoint;

    private Boolean isActive = true;

    // ===================== LOGÍSTICA =====================
    private String locationCode;
    private Double weight;
    private Double volume;
    private LocalDate expirationDate;
    private String batchNumber;

    // ===================== FISCAL =====================
    @Column(precision = 5, scale = 2)
    private BigDecimal taxPercentage;

    private Boolean isTaxIncluded;

    private String accountingCode;

    // ===================== E-COMMERCE =====================
    private String brand;
    private String model;
    private String tags;
    private String imageUrl;

    // ===================== RELACIONAMENTOS =====================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // ===================== AUDITORIA =====================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // =========================================================
    // ===================== MÉTODOS CÁLCULOS ==================
    // =========================================================

    /**
     * Quantas caixas completas existem.
     */
    public int getFullBoxes() {
        if (boxes == null || boxes <= 0 || quantityInStock == null) return 0;
        return quantityInStock / boxes;
    }

    /**
     * Quantos itens soltos restam depois das caixas completas.
     */
    public int getRemainingItems() {
        if (boxes == null || boxes <= 0 || quantityInStock == null) return 0;
        return quantityInStock % boxes;
    }

    /**
     * Detalhe formatado da quantidade no estoque.
     */
    public String getStockDetail() {
        return String.format("%d caixas + %d %s", getFullBoxes(), getRemainingItems(), unitOfMeasure);
    }

    /**
     * Se o produto está abaixo do stock mínimo.
     */
    public boolean isBelowMinimum() {
        if (minimumStockLevel == null || quantityInStock == null) return false;
        return quantityInStock < minimumStockLevel;
    }

    /**
     * Margem de lucro absoluta.
     */
    public BigDecimal getProfitMargin() {
        if (sellingPrice == null || costPrice == null) return BigDecimal.ZERO;
        return sellingPrice.subtract(costPrice).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Margem de lucro percentual.
     */
    public BigDecimal getProfitMarginPercentage() {
        if (sellingPrice == null || costPrice == null || costPrice.compareTo(BigDecimal.ZERO) == 0)
            return BigDecimal.ZERO;

        BigDecimal margin = sellingPrice.subtract(costPrice);

        return margin
                .divide(costPrice, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
