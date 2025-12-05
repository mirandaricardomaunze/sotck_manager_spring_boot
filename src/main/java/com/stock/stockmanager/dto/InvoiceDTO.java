package com.stock.stockmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceDTO {
    private Long id;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
    private BigDecimal totalAmount;
    private String status;

    private String customerName;
    private String deliveryAddress;
    private String customerEmail;
    private String customerContact;
    private String customerNuit;
    private String paymentMethod;

    private String companyName;
    private String warehouseName;

    private Long orderId; // ID da encomenda original

    private List<InvoiceItemDTO> items;
    private String notes;
}
