package com.stock.stockmanager.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemRequestDTO {
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice; // opcional, se não for fornecido pega do produto
    private BigDecimal taxAmount;
    private  BigDecimal subtotal;


    public boolean isValid() {
        return productId != null && productId > 0
                && quantity != null && quantity > 0
                && (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) > 0);
    }
}
