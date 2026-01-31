package com.stock.stockmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponseDTO {
    private Long id;

    // Produto transferido
    private Long productId;
    private String productName;

    // Armazéns de origem e destino
    private Long sourceWarehouseId;
    private String sourceWarehouse;
    private Long destinationWarehouseId;
    private String destinationWarehouse;

    // Empresa
    private Long companyId;
    private String companyName;

    // Utilizador
    private Long userId;
    private String user;

    // Quantidade transferida
    private Integer quantity;

    // Estoque após a transferência em cada armazém
    private StockResponseDTO stockSource;
    private StockResponseDTO stockDestination;

    // Data da transferência
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    private LocalDateTime transferDate;

    // Referência opcional da transferência
    private String reference;
}
