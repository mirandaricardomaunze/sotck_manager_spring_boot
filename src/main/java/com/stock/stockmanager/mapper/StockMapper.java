package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.StockResponseDTO;
import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    public StockResponseDTO toDTO(Stock stock) {
        if (stock == null) return null;
        StockResponseDTO dto = new StockResponseDTO();

        dto.setId(stock.getId());  // ≤-- ‘ID’ agora é populado

        if (stock.getProduct() != null) {
            dto.setProductId(stock.getProduct().getId());
            dto.setProductName(stock.getProduct().getName());
        }

        if (stock.getWarehouse() != null) {
            dto.setWarehouseId(stock.getWarehouse().getId());
            dto.setWarehouseName(stock.getWarehouse().getName());
        }

        dto.setQuantity(stock.getQuantity());
        dto.setReservedQuantity(stock.getReservedQuantity());
        dto.setAvailableQuantity(stock.getAvailableQuantity()); // quantity - reservedQuantity

        return dto;
    }

    public StockSummaryDTO toSummaryDTO(String name, Long totalQuantity) {
        return new StockSummaryDTO(name, totalQuantity);
    }
}
