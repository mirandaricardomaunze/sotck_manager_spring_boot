package com.stock.stockmanager.dto;

import com.stock.stockmanager.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequestDTO {

    @NotBlank(message = "O nome do cliente é obrigatório")
    private String clientName;
    @NotNull(message = "Armazém é obrigatório")
    private Long warehouseId;
    @NotNull(message = "A empresa é obrigatória")
    private Long companyId;
    @NotNull(message = "O valor pago é obrigatório")
    private BigDecimal amountPaid;

    private BigDecimal discount; // opcional

    @NotNull(message = "O método de pagamento é obrigatório")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Itens da venda são obrigatórios")
    private List<SaleItemRequestDTO> items;
    private Long userId;
    private String userName;
    // Validação básica
    public boolean isValid() {
        return clientName != null && !clientName.isBlank()
                && companyId != null && companyId > 0
                && amountPaid != null && amountPaid.compareTo(BigDecimal.ZERO) >= 0
                && paymentMethod != null
                && items != null && !items.isEmpty()
                && items.stream().allMatch(SaleItemRequestDTO::isValid);
    }
}
