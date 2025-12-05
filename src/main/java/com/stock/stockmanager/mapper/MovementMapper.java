package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.MovementRequestDTO;
import com.stock.stockmanager.dto.MovementResponseDTO;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.enums.MovementStatusType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MovementMapper {

    /**
     * Converte DTO → Entity
     * Recebe company, warehouse, product e user como parâmetros
     */
    public Movement toEntity(MovementRequestDTO dto, Company company, Warehouse warehouse, Product product, User user) {
        if (dto == null) return null;

        return Movement.builder()
                .description(dto.getDescription())
                .type(dto.getType())
                .origin(dto.getOrigin())
                .status(dto.getStatus() != null ? dto.getStatus() : MovementStatusType.PENDING)
                .quantity(dto.getQuantity())
                .referenceNumber(dto.getReferenceNumber())
                .company(company)
                .warehouse(warehouse)
                .product(product)
                .user(user) // associação correta com User
                .date(dto.getDate() != null ? dto.getDate() : LocalDateTime.now())
                .build();
    }

    /**
     * Converte Entity → DTO
     * Retorna username para exibir no frontend
     */
    public MovementResponseDTO toDTO(Movement movement) {
        if (movement == null) return null;

        return MovementResponseDTO.builder()
                .id(movement.getId())
                .description(movement.getDescription())
                .type(movement.getType())
                .origin(movement.getOrigin())
                .status(movement.getStatus())
                .quantity(movement.getQuantity())
                .date(movement.getDate())
                .referenceNumber(movement.getReferenceNumber())
                .companyId(movement.getCompany() != null ? movement.getCompany().getId() : null)
                .warehouseId(movement.getWarehouse() != null ? movement.getWarehouse().getId() : null)
                .productId(movement.getProduct() != null ? movement.getProduct().getId() : null)
                .username(movement.getUser() != null ? movement.getUser().getUsername() : null) // retorna username
                .build();
    }
}
