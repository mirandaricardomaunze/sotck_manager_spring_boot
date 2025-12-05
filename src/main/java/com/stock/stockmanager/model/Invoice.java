package com.stock.stockmanager.model;

import com.stock.stockmanager.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;       // Mesmo número da encomenda
    private LocalDateTime invoiceDate;  // Data de emissão
    private BigDecimal totalAmount;     // Valor total

    @Enumerated(EnumType.STRING)
    private  InvoiceStatus status;             // PAGO, PENDENTE, CANCELADO

    // Dados do cliente (copiados da encomenda)
    private String customerName;
    private String deliveryAddress;
    private String customerEmail;
    private String customerContact;
    private String customerNuit;
    private String paymentMethod;

    // Dados da empresa / armazém
    private String companyName;
    private String warehouseName;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order; // Associação com a encomenda original

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceItem> items; // Itens da fatura

    private String notes;

    @PrePersist
    public void prePersist() {
        if (this.invoiceNumber == null || this.invoiceNumber.isEmpty()) {
            if (order != null && order.getOrderNumber() != null) {
                this.invoiceNumber = order.getOrderNumber(); // usa o mesmo número da encomenda
            } else {
                this.invoiceNumber = "INV-" + System.currentTimeMillis();
            }
        }
        if (this.invoiceDate == null) {
            this.invoiceDate = LocalDateTime.now();
        }
    }
}
