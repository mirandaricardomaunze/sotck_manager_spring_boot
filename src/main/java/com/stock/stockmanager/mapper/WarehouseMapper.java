package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.WarehouseRequestDTO;
import com.stock.stockmanager.dto.WarehouseResponseDTO;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.enums.WarehouseStatus;
import org.springframework.stereotype.Component;

@Component
public class WarehouseMapper {

    // RequestDTO → Entity
    public Warehouse toEntity(WarehouseRequestDTO dto, Company company) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setDescription(dto.getDescription());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setEmail(dto.getEmail());
        warehouse.setPhone(dto.getPhone());
        warehouse.setManager(dto.getManager());
        warehouse.setStatus(dto.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE);
        warehouse.setPrincipal(dto.isPrincipal());
        warehouse.setCompany(company);
        return warehouse;
    }

    // Entity → ResponseDTO
    public WarehouseResponseDTO toResponseDTO(Warehouse warehouse) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setDescription(warehouse.getDescription());
        dto.setCapacity(warehouse.getCapacity());
        dto.setEmail(warehouse.getEmail());
        dto.setPhone(warehouse.getPhone());
        dto.setManager(warehouse.getManager());
        dto.setActive(warehouse.getStatus() == WarehouseStatus.ACTIVE);
        dto.setPrincipal(warehouse.isPrincipal());
        dto.setCompanyId(warehouse.getCompany().getId());
        dto.setCompanyName(warehouse.getCompany().getName());
        return dto;
    }

    // Atualizar Entity existente com dados do RequestDTO
    public void updateEntityFromDTO(WarehouseRequestDTO dto, Warehouse warehouse, Company company) {
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setDescription(dto.getDescription());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setEmail(dto.getEmail());
        warehouse.setPhone(dto.getPhone());
        warehouse.setManager(dto.getManager());
        warehouse.setStatus(dto.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE);
        warehouse.setPrincipal(dto.isPrincipal());
        warehouse.setCompany(company);
    }
}
