package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.SaleItemReportDTO;
import com.stock.stockmanager.model.SaleItem;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MapperReportItems {

    private MapperReportItems() {
        // evita instanciação
    }

    public static List<SaleItemReportDTO> map(List<SaleItem> items) {

        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }

        return items.stream()
                .map(item -> {
                    String productName = item.getProduct() != null
                            ? item.getProduct().getName()
                            : "Produto não informado";

                    int quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    java.math.BigDecimal unitPrice = item.getUnitPrice() != null ? item.getUnitPrice() : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal subtotal = item.getSubtotal() != null ? item.getSubtotal() : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal taxAmount = item.getTaxAmount() != null ? item.getTaxAmount() : java.math.BigDecimal.ZERO;
                    java.math.BigDecimal total = subtotal.add(taxAmount);

                    return new SaleItemReportDTO(
                            productName,
                            quantity,
                            unitPrice,
                            subtotal,
                            taxAmount,
                            total
                    );
                })
                .collect(Collectors.toList());
    }
}
