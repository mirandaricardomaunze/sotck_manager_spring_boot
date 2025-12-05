package com.stock.stockmanager.dto;

import com.stock.stockmanager.enums.MovementOrigin;
import com.stock.stockmanager.enums.MovementStatusType;
import com.stock.stockmanager.enums.MovementType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementResponseDTO {
    private Long id;
    private String description;
    private MovementType type;
    private MovementOrigin origin;
    private MovementStatusType status;
    private Integer quantity;
    private LocalDateTime date;
    private String userId;  // aqui vai o username
    private String username;
    private String referenceNumber;
    private Long companyId;
    private Long warehouseId;
    private Long productId;
}
