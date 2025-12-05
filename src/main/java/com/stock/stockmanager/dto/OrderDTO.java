package com.stock.stockmanager.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDTO {
    private Long id;
    @JsonProperty("companyId")
    private Long companyId;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("warehouseId")
    private Long warehouseId;
    @JsonProperty("warehouseName")
    private String warehouseName;
    private String orderNumber;
    private String deliveryAddress;
    private LocalDateTime orderDate;
    private String customerName;
    private String customerContact;
    private String customerEmail;
    private String paymentMethod;
    private String customerNuit;
    private String status;
    private String notes;
    @JsonProperty("items")
    private List<OrderItemDTO> items = new ArrayList<>();
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    public void calculateTotalOrder() {
        if (items == null || items.isEmpty()) {
            totalAmount = BigDecimal.ZERO;
            return;
        }
        totalAmount = items.stream()
                .map(item -> {
                    if (item.getTotalPrice() == null) item.calculateTotalPrice();
                    return item.getTotalPrice();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
