package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.ProductRequestDTO;
import com.stock.stockmanager.dto.ProductResponseDTO;
import com.stock.stockmanager.model.*;

public class ProductMapper {

    // =========================================================
    // ===================== REQUEST → ENTITY ==================
    // =========================================================
    public static Product fromRequestDTO(
            ProductRequestDTO dto,
            Company company,
            Warehouse warehouse,
            Category category,
            Supplier supplier
    ) {
        if (dto == null) return null;

        return Product.builder()
                // ===== Básico =====
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .barcode(dto.getBarcode())
                .referenceNumber(dto.getReferenceNumber())
                .boxes(dto.getBoxes())
                .sellingPrice(dto.getSellingPrice())
                .costPrice(dto.getCostPrice())
                .quantityInStock(dto.getQuantityInStock())
                .minimumStockLevel(dto.getMinimumStockLevel())
                .maximumStockLevel(dto.getMaximumStockLevel())
                .reorderPoint(dto.getReorderPoint())
                .unitOfMeasure(dto.getUnitOfMeasure())

                // ===== Fiscal =====
                .taxPercentage(dto.getTaxPercentage())
                .isTaxIncluded(dto.getIsTaxIncluded())
                .accountingCode(dto.getAccountingCode())

                // ===== Logística =====
                .locationCode(dto.getLocationCode())
                .weight(dto.getWeight())
                .volume(dto.getVolume())
                .expirationDate(dto.getExpirationDate())
                .batchNumber(dto.getBatchNumber())

                // ===== Comercial =====
                .brand(dto.getBrand())
                .model(dto.getModel())
                .tags(dto.getTags())
                .imageUrl(dto.getImageUrl())

                // ===== Relacionamentos =====
                .company(company)
                .warehouse(warehouse)
                .category(category)
                .supplier(supplier)

                // ===== Estado =====
                .isActive(true)
                .build();
    }

    // =========================================================
    // ===================== UPDATE ENTITY =====================
    // =========================================================
    public static void updateEntityFromRequestDTO(
            Product product,
            ProductRequestDTO dto,
            Company company,
            Warehouse warehouse,
            Category category,
            Supplier supplier
    ) {
        if (product == null || dto == null) return;

        // ===== Básico =====
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBoxes(dto.getBoxes());
        product.setSellingPrice(dto.getSellingPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setMinimumStockLevel(dto.getMinimumStockLevel());
        product.setMaximumStockLevel(dto.getMaximumStockLevel());
        product.setReorderPoint(dto.getReorderPoint());
        product.setUnitOfMeasure(dto.getUnitOfMeasure());

        // ===== Fiscal =====
        product.setTaxPercentage(dto.getTaxPercentage());
        product.setIsTaxIncluded(dto.getIsTaxIncluded());
        product.setAccountingCode(dto.getAccountingCode());

        // ===== Logística =====
        product.setLocationCode(dto.getLocationCode());
        product.setWeight(dto.getWeight());
        product.setVolume(dto.getVolume());
        product.setExpirationDate(dto.getExpirationDate());
        product.setBatchNumber(dto.getBatchNumber());

        // ===== Comercial =====
        product.setBrand(dto.getBrand());
        product.setModel(dto.getModel());
        product.setTags(dto.getTags());
        product.setImageUrl(dto.getImageUrl());

        // ===== Relacionamentos =====
        if (company != null) product.setCompany(company);
        if (warehouse != null) product.setWarehouse(warehouse);
        if (category != null) product.setCategory(category);
        if (supplier != null) product.setSupplier(supplier);
    }

    // =========================================================
    // ===================== ENTITY → RESPONSE =================
    // =========================================================
    public static ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) return null;

        return ProductResponseDTO.builder()
                // ===== Identificação =====
                .id(product.getId())

                // ===== Básico =====
                .name(product.getName())
                .description(product.getDescription())
                .sku(product.getSku())
                .barcode(product.getBarcode())
                .referenceNumber(product.getReferenceNumber())
                .boxes(product.getBoxes())
                .sellingPrice(product.getSellingPrice())
                .costPrice(product.getCostPrice())
                .quantityInStock(product.getQuantityInStock())
                .minimumStockLevel(product.getMinimumStockLevel())
                .maximumStockLevel(product.getMaximumStockLevel())
                .reorderPoint(product.getReorderPoint())
                .unitOfMeasure(product.getUnitOfMeasure())
                .isActive(product.getIsActive())

                // ===== Fiscal =====
                .taxPercentage(product.getTaxPercentage())
                .isTaxIncluded(product.getIsTaxIncluded())
                .accountingCode(product.getAccountingCode())

                // ===== Logística =====
                .locationCode(product.getLocationCode())
                .weight(product.getWeight())
                .volume(product.getVolume())
                .expirationDate(product.getExpirationDate())
                .batchNumber(product.getBatchNumber())

                // ===== Comercial =====
                .brand(product.getBrand())
                .model(product.getModel())
                .tags(product.getTags())
                .imageUrl(product.getImageUrl())

                // ===== Relacionamentos (seguros) =====
                .companyId(product.getCompany() != null ? product.getCompany().getId() : null)
                .companyName(product.getCompany() != null ? product.getCompany().getName() : null)

                .warehouseId(product.getWarehouse() != null ? product.getWarehouse().getId() : null)
                .warehouseName(product.getWarehouse() != null ? product.getWarehouse().getName() : null)

                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)

                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)

                // ===== Métricas calculadas =====
                .fullBoxes(product.getFullBoxes())
                .remainingItems(product.getRemainingItems())
                .stockDetail(product.getStockDetail())
                .profitMargin(product.getProfitMargin())
                .profitMarginPercentage(product.getProfitMarginPercentage())

                // ===== Auditoria =====
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
