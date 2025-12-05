package com.stock.stockmanager.dto;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesDTO {
    private Long productId;
    private String productName;
    private String productBarcode;
    private Long unitsSold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
}