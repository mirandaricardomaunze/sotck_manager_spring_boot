package com.stock.stockmanager.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
public class CreditNoteDTO {
    private Long id;
    private String creditNoteNumber;
    private LocalDateTime creditNoteDate;
    private BigDecimal totalAmount;
    private Long invoiceId;
    private List<CreditNoteItemDTO> items;
    private String notes;
}
