package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.SupplierDTO;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Supplier;

public class SupplierMapper {

    // Entity → DTO
    public static SupplierDTO toDTO(Supplier supplier) {
        if (supplier == null) return null;
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getNuit(),
                supplier.getAddress(),
                supplier.getWebsite(),
                supplier.getNotes(),
                supplier.getCompany() != null ? supplier.getCompany().getId() : null,
                supplier.getCompany() != null ? supplier.getCompany().getName() : null
        );
    }

    // DTO → Entity
    public static Supplier toEntity(SupplierDTO dto, Company company) {
        if (dto == null) return null;
        Supplier supplierEntity = new Supplier();
        supplierEntity.setId(dto.getId());
        supplierEntity.setName(dto.getName());
        supplierEntity.setEmail(dto.getEmail());
        supplierEntity.setPhone(dto.getPhone());
        supplierEntity.setNuit(dto.getNuit());
        supplierEntity.setAddress(dto.getAddress());
        supplierEntity.setWebsite(dto.getWebsite());
        supplierEntity.setNotes(dto.getNotes());
        supplierEntity.setCompany(company);
        return supplierEntity;
    }
}
