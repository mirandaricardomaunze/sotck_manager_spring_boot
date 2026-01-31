package com.stock.stockmanager.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    // ===================== DADOS BÁSICOS =====================

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
    private String name;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 500, message = "A descrição pode ter no máximo 500 caracteres")
    private String description;

    @NotBlank(message = "O SKU é obrigatório")
    @Size(max = 100, message = "O SKU pode ter no máximo 100 caracteres")
    private String sku;

    @NotBlank(message = "O código de barras é obrigatório")
    @Size(max = 100, message = "O código de barras pode ter no máximo 100 caracteres")
    private String barcode;

    @NotBlank(message = "O número de referência é obrigatório")
    @Size(max = 100, message = "O número de referência pode ter no máximo 100 caracteres")
    private String referenceNumber;

    @NotNull(message = "A quantidade por caixa é obrigatória")
    @Positive(message = "A quantidade por caixa deve ser positiva")
    private Integer boxes;

    @NotNull(message = "O preço de venda é obrigatório")
    @DecimalMin(value = "0.00", inclusive = false, message = "O preço de venda deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Preço de venda inválido")
    private BigDecimal sellingPrice;

    @NotNull(message = "O preço de custo é obrigatório")
    @DecimalMin(value = "0.00", inclusive = false, message = "O preço de custo deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Preço de custo inválido")
    private BigDecimal costPrice;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
    private Integer quantityInStock;

    @NotNull(message = "O nível mínimo de estoque é obrigatório")
    @PositiveOrZero(message = "O nível mínimo de estoque não pode ser negativo")
    private Integer minimumStockLevel;

    @PositiveOrZero(message = "O nível máximo de estoque não pode ser negativo")
    private Integer maximumStockLevel;

    @PositiveOrZero(message = "O ponto de reposição não pode ser negativo")
    private Integer reorderPoint;

    @NotBlank(message = "A unidade de medida é obrigatória")
    @Size(max = 20, message = "A unidade de medida pode ter no máximo 20 caracteres")
    private String unitOfMeasure;

    // ===================== FISCAL =====================

    @DecimalMin(value = "0.00", message = "A taxa de imposto não pode ser negativa")
    @DecimalMax(value = "100.00", message = "A taxa de imposto não pode ultrapassar 100%")
    @Digits(integer = 3, fraction = 2, message = "Taxa de imposto inválida")
    private BigDecimal taxPercentage;

    @NotNull(message = "Informe se o imposto está incluído no preço")
    private Boolean isTaxIncluded;

    @Size(max = 50, message = "O código contabilístico pode ter no máximo 50 caracteres")
    private String accountingCode;

    // ===================== LOGÍSTICA =====================

    @Size(max = 50, message = "O código de localização pode ter no máximo 50 caracteres")
    private String locationCode;

    @Positive(message = "O peso deve ser positivo")
    private Double weight;

    @Positive(message = "O volume deve ser positivo")
    private Double volume;

    @FutureOrPresent(message = "A data de validade deve ser hoje ou futura")
    private LocalDate expirationDate;

    @Size(max = 50, message = "O número do lote pode ter no máximo 50 caracteres")
    private String batchNumber;

    // ===================== COMERCIAL / E-COMMERCE =====================

    @Size(max = 100, message = "A marca pode ter no máximo 100 caracteres")
    private String brand;

    @Size(max = 100, message = "O modelo pode ter no máximo 100 caracteres")
    private String model;

    @Size(max = 255, message = "As tags podem ter no máximo 255 caracteres")
    private String tags;

    @Size(max = 255, message = "A URL da imagem pode ter no máximo 255 caracteres")
    private String imageUrl;

    // ===================== RELACIONAMENTOS =====================

    @NotNull(message = "A empresa é obrigatória")
    private Long companyId;

    @NotNull(message = "O armazém é obrigatório")
    private Long warehouseId;

    @NotNull(message = "A categoria é obrigatória")
    private Long categoryId;

    private Long supplierId;
}
