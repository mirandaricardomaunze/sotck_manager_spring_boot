package com.stock.stockmanager.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private Boolean isActive;
    private String referenceNumber;
    private Integer boxes;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private Integer quantityInStock;
    private Integer minimumStockLevel;
    private String unitOfMeasure;

    // Relacionamentos (apenas ids/nome)
    private Long companyId;
    private String companyName;
    private Long warehouseId;
    private String warehouseName;
    private Long categoryId;
    private String categoryName;
    private Long supplierId;
    private String supplierName;

    // Métodos avançados da entidade
    private int fullBoxes;
    private int remainingItems;
    private String stockDetail;
    private Boolean belowMinimum;
    private BigDecimal profitMargin;
    private BigDecimal profitMarginPercentage;


}
