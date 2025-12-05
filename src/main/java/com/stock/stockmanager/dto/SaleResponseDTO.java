package com.stock.stockmanager.dto;

import com.stock.stockmanager.enums.PaymentMethod;
import com.stock.stockmanager.enums.SaleStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {
    private Long id;
    private String saleCode;
    private String clientName;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal amountPaid;
    private BigDecimal change;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private LocalDateTime saleDate;
    private List<SaleItemResponseDTO> items;
    private Long companyId;
}
