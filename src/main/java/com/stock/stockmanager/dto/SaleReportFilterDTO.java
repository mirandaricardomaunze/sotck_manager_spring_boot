package com.stock.stockmanager.dto;

import com.stock.stockmanager.enums.PaymentMethod;
import com.stock.stockmanager.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleReportFilterDTO {

    private String clientName;
    private SaleStatus status;
    private PaymentMethod paymentMethod;

}
