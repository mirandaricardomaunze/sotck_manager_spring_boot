package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.StockResponseDTO;
import com.stock.stockmanager.dto.TransferResponseDTO;
import com.stock.stockmanager.model.Stock;
import com.stock.stockmanager.model.Transfer;
import org.springframework.stereotype.Component;

@Component
public class TransferMapper {

    // ==============================
    // ENTIDADE → RESPONSE DTO
    // ==============================
    public TransferResponseDTO toResponseDTO(Transfer transfer,
                                             Stock stockOrigin,
                                             Stock stockDestination) {
        if (transfer == null) return null;

        TransferResponseDTO dto = new TransferResponseDTO();

        dto.setId(transfer.getId());

        // Produto
        if (transfer.getProduct() != null) {
            dto.setProductName(transfer.getProduct().getName());
            dto.setProductId(transfer.getProduct().getId());
        }

        // Empresa
        if (transfer.getCompany() != null) {
            dto.setCompanyName(transfer.getCompany().getName());
            dto.setCompanyId(transfer.getCompany().getId());
        }

        // Armazéns
        if (transfer.getSourceWarehouse() != null) {
            dto.setSourceWarehouse(transfer.getSourceWarehouse().getName());
            dto.setSourceWarehouseId(transfer.getSourceWarehouse().getId());
        }
        if (transfer.getDestinationWarehouse() != null) {
            dto.setDestinationWarehouse(transfer.getDestinationWarehouse().getName());
            dto.setDestinationWarehouseId(transfer.getDestinationWarehouse().getId());
        }

        dto.setQuantity(transfer.getQuantity());
        dto.setTransferDate(transfer.getTransferDate());
        dto.setReference(transfer.getReference());

        // Usuário
        if (transfer.getUser() != null) {
            dto.setUser(transfer.getUser().getUsername()); // Nome do usuário
            dto.setUserId(transfer.getUser().getId());      // ID do usuário ✅
        }

        dto.setStockSource(toStockDTO(stockOrigin));
        dto.setStockDestination(toStockDTO(stockDestination));

        return dto;
    }

    // ==============================
    // STOCK → STOCK DTO
    // ==============================
    private StockResponseDTO toStockDTO(Stock stock) {
        if (stock == null) return null;
        StockResponseDTO s = new StockResponseDTO();

        s.setId(stock.getId());
        s.setQuantity(stock.getQuantity());

        if (stock.getWarehouse() != null) {
            s.setWarehouseName(stock.getWarehouse().getName());
            s.setWarehouseId(stock.getWarehouse().getId());
        }

        if (stock.getProduct() != null) {
            s.setProductName(stock.getProduct().getName());
            s.setProductId(stock.getProduct().getId());
        }

        return s;
    }
}
