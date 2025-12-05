package com.stock.stockmanager.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportDTO {
    private String period;
    private BigDecimal totalRevenue;
    private Long totalSales;
    private List<SaleResponseDTO> sales;
}
