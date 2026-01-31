package com.stock.stockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
    private Long id;
    private Long productId;
    private String productName;

    private Long warehouseId;
    private String warehouseName;

    private Integer quantity;          // estoque real
    private Integer reservedQuantity;  // estoque reservado
    private Integer availableQuantity;
}
