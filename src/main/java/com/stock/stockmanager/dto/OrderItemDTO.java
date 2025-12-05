package com.stock.stockmanager.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderItemDTO {

    private Long id; // Unique identifier for the order item

    @JsonProperty("productId")
    private Long productId; // ID of the product

    @JsonProperty("productName")
    private String productName; // Name of the product

    private Integer quantity; // Quantity of the product ordered

    private BigDecimal unitPrice; // Price per unit of the product

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice; // Total price for this order item

    // Method to calculate total price for the item
    public void calculateTotalPrice() {
        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            totalPrice = BigDecimal.ZERO; // Default to zero if unit price or quantity is null
        }
    }
}