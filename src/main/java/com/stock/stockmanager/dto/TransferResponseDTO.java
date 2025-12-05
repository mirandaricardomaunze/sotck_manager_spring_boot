package com.stock.stockmanager.dto;

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

    // Usuário
    private Long userId;
    private String user;

    // Quantidade transferida
    private Integer quantity;

    // Estoque após a transferência em cada armazém
    private StockDTO stockSource;
    private StockDTO stockDestination;

    // Data da transferência
    private LocalDateTime transferDate;

    // Referência opcional da transferência
    private String reference;
}
