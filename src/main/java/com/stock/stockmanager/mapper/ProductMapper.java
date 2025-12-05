package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.ProductRequestDTO;
import com.stock.stockmanager.dto.ProductResponseDTO;
import com.stock.stockmanager.model.*;

import java.math.RoundingMode;

public class ProductMapper {

    // ===================== DTO → ENTIDADE =====================
    public static Product fromRequestDTO(ProductRequestDTO dto, Company company,
                                         Warehouse warehouse, Category category, Supplier supplier) {
        if (dto == null) return null;

        return Product.builder()
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
                .unitOfMeasure(dto.getUnitOfMeasure())
                .company(company)
                .warehouse(warehouse)
                .category(category)
                .supplier(supplier)
                .isActive(true)
                .build();
    }

    // ===================== ATUALIZA ENTIDADE =====================
    public static void updateEntityFromRequestDTO(Product product, ProductRequestDTO dto, Company company,
                                                  Warehouse warehouse, Category category, Supplier supplier) {
        if (product == null || dto == null) return;

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setSku(dto.getSku());
        product.setBarcode(dto.getBarcode());
        product.setReferenceNumber(dto.getReferenceNumber());
        product.setBoxes(dto.getBoxes());
        product.setSellingPrice(dto.getSellingPrice());
        product.setCostPrice(dto.getCostPrice());
        product.setQuantityInStock(dto.getQuantityInStock());
        product.setMinimumStockLevel(dto.getMinimumStockLevel());
        product.setUnitOfMeasure(dto.getUnitOfMeasure());
        product.setCompany(company);
        product.setWarehouse(warehouse);
        product.setCategory(category);
        product.setSupplier(supplier);
    }

    // ===================== ENTIDADE → RESPONSE DTO =====================
    public static ProductResponseDTO toResponseDTO(Product product) {
        if (product == null) return null;

        ProductResponseDTO dto = ProductResponseDTO.builder()
                .id(product.getId())
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
                .unitOfMeasure(product.getUnitOfMeasure())
                .isActive(product.getIsActive())
                // Relacionamentos apenas com nomes ou IDs para evitar loop infinito
                .companyId(product.getCompany() != null ? product.getCompany().getId() : null)
                .companyName(product.getCompany() != null ? product.getCompany().getName() : null)
                .warehouseId(product.getWarehouse() != null ? product.getWarehouse().getId() : null)
                .warehouseName(product.getWarehouse() != null ? product.getWarehouse().getName() : null)
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .supplierId(product.getSupplier() != null ? product.getSupplier().getId() : null)
                .supplierName(product.getSupplier() != null ? product.getSupplier().getName() : null)
                // Campos avançados calculados
                .fullBoxes(product.getFullBoxes())
                .remainingItems(product.getRemainingItems())
                .stockDetail(product.getStockDetail())
                .profitMargin(product.getProfitMargin())
                .profitMarginPercentage(product.getProfitMarginPercentage())
                .build();

        return dto;
    }

}
