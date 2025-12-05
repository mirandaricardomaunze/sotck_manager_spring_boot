package com.stock.stockmanager.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockRequestDTO {
    private Long productId;
    private Long warehouseId;
    private Integer quantity;
}
