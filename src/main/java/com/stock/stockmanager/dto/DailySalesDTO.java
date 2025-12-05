package com.stock.stockmanager.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySalesDTO {
    private LocalDate date;
    private Long salesCount;
    private BigDecimal revenue;
    private BigDecimal averageSale;
}