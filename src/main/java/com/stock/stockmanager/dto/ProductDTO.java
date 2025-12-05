package com.stock.stockmanager.dto;

import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.model.Supplier;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    private String name;

    private String description;

    @Min(value = 0, message = "O número de caixas não pode ser negativo")
    private Integer boxes;

    @NotBlank(message = "O SKU é obrigatório")
    private String sku;

    @Min(value = 0, message = "O preço de venda não pode ser negativo")
    private Double sellingPrice;

    @Min(value = 0, message = "O custo não pode ser negativo")
    private Double costPrice;

    @Min(value = 0, message = "A quantidade em estoque não pode ser negativa")
    private Integer quantityInStock;

    @Min(value = 0, message = "O nível mínimo de estoque não pode ser negativo")
    private Integer minimumStockLevel;

    private String unitOfMeasure;

    private String barcode;

    private String referenceNumber;

    @NotNull(message = "A empresa é obrigatória")
    private Long companyId;
    private String company; // Para exibição no frontend

    @NotNull(message = "O armazém é obrigatório")
    private Long warehouseId;
    private String warehouse; // Para exibição no frontend

    private Long supplierId; // Pode ser null
    private String supplier; // Para exibição no frontend

    @NotBlank(message = "A categoria é obrigatória")
    private Long categoryId;
    private String category;
}
