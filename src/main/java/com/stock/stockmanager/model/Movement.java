package com.stock.stockmanager.model;

import com.stock.stockmanager.enums.MovementOrigin;
import com.stock.stockmanager.enums.MovementStatusType;
import com.stock.stockmanager.enums.MovementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @NotNull(message = "Tipo de movimento é obrigatório")
    @Enumerated(EnumType.STRING)
    private MovementType type;

    @NotNull(message = "Origem do movimento é obrigatória")
    @Enumerated(EnumType.STRING)
    private MovementOrigin origin;

    @NotNull(message = "Status do movimento é obrigatório")
    @Enumerated(EnumType.STRING)
    private MovementStatusType status;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser maior que zero")
    private Integer quantity;

    @NotNull(message = "Data é obrigatória")
    private LocalDateTime date;

    private String referenceNumber;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        if (date == null) date = LocalDateTime.now();
        if (status == null) status = MovementStatusType.PENDING;
    }
}
