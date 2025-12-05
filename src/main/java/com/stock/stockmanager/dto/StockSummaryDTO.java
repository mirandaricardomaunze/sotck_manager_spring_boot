package com.stock.stockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSummaryDTO {
    private String name;        // Nome do produto ou armazém
    private Long totalQuantity; // Sempre Long por causa do SUM()

}
