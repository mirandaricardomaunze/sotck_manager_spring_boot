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
public class MovementRequestDTO {
    private String description;
    private MovementType type;
    private MovementOrigin origin;
    private MovementStatusType status;
    private Integer quantity;
    private LocalDateTime date;
    private String referenceNumber;
    private Long companyId;
    private Long warehouseId;
    private Long productId;
    private Long userId;
}
