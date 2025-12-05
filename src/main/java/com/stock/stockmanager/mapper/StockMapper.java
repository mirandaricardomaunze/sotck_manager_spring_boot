package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.StockDTO;
import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    // Mapper para StockDTO (com IDs e nomes)
    public StockDTO toDTO(Stock stock) {
        if (stock == null) return null;
        StockDTO dto = new StockDTO();
        dto.setId(stock.getId());

        if (stock.getProduct() != null) {
            dto.setProductId(stock.getProduct().getId());
            dto.setProductName(stock.getProduct().getName());
        }

        if (stock.getWarehouse() != null) {
            dto.setWarehouseId(stock.getWarehouse().getId());
            dto.setWarehouseName(stock.getWarehouse().getName());
        }

        dto.setQuantity(stock.getQuantity());
        return dto;
    }

    // Mapper para StockSummaryDTO
    public StockSummaryDTO toSummaryDTO(String name, Long totalQuantity) {
        return new StockSummaryDTO(name, totalQuantity);
    }
}
