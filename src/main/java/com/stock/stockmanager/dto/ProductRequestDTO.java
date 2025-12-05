package com.stock.stockmanager.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    // ===================== DADOS BÁSICOS =====================
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotBlank(message = "SKU é obrigatório")
    private String sku;

    @NotBlank(message = "Código de barras é obrigatório")
    private String barcode;

    @NotBlank(message = "Número de referência é obrigatório")
    private String referenceNumber;

    @NotNull(message = "Número de itens por caixa é obrigatório")
    @Min(value = 1, message = "Itens por caixa deve ser maior que 0")
    private Integer boxes;

    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de venda deve ser maior que 0")
    private BigDecimal sellingPrice;

    @NotNull(message = "Preço de custo é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false, message = "Preço de custo deve ser maior que 0")
    private BigDecimal costPrice;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    private Integer quantityInStock;

    @NotNull(message = "Nível mínimo de estoque é obrigatório")
    @Min(value = 0, message = "Nível mínimo não pode ser negativo")
    private Integer minimumStockLevel;

    @NotBlank(message = "Unidade de medida é obrigatória")
    private String unitOfMeasure;

    // ===================== RELACIONAMENTOS =====================
    @NotNull(message = "ID da empresa é obrigatório")
    private Long companyId;

    @NotNull(message = "ID do armazém é obrigatório")
    private Long warehouseId;

    @NotNull(message = "ID da categoria é obrigatório")
    private Long categoryId;

    // Fornecedor é opcional
    private Long supplierId;


}
