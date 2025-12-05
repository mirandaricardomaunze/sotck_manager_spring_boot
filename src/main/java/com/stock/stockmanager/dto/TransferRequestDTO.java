package com.stock.stockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private Long id;
    // Empresa que realiza a transferência
    private Long companyId;

    // Produto a ser transferido
    private Long productId;

    // Armazém de origem
    private Long sourceWarehouseId;

    // Armazém de destino
    private Long destinationWarehouseId;

    // Quantidade a ser transferida
    private Integer quantity;

    // Referência opcional (ex: número de nota ou observação)
    private String reference;
}
