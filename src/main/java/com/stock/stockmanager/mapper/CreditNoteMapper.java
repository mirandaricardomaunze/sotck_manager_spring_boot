package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.CreditNoteDTO;
import com.stock.stockmanager.dto.CreditNoteItemDTO;
import com.stock.stockmanager.model.CreditNote;
import com.stock.stockmanager.model.CreditNoteItem;
import com.stock.stockmanager.model.Invoice;

import java.math.BigDecimal;
import java.util.List;

public class CreditNoteMapper {

    public static CreditNote fromInvoice(Invoice invoice, List<CreditNoteItem> items) {
        CreditNote cn = new CreditNote();
        cn.setInvoice(invoice);
        cn.setItems(items);

        BigDecimal total = items.stream()
                .map(item -> item.getTotalPrice() != null
                        ? item.getTotalPrice()
                        : (item.getUnitPrice() != null && item.getQuantity() != null
                        ? item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                        : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cn.setTotalAmount(total);
        return cn;
    }

    public static CreditNoteDTO toDTO(CreditNote creditNote) {
        CreditNoteDTO dto = new CreditNoteDTO();
        dto.setId(creditNote.getId());
        dto.setCreditNoteNumber(creditNote.getCreditNoteNumber());
        dto.setCreditNoteDate(creditNote.getCreditNoteDate());
        dto.setTotalAmount(creditNote.getTotalAmount());
        dto.setInvoiceId(creditNote.getInvoice() != null ? creditNote.getInvoice().getId() : null);

        if (creditNote.getItems() != null) {
            dto.setItems(creditNote.getItems().stream().map(item -> {
                CreditNoteItemDTO itemDTO = new CreditNoteItemDTO();
                itemDTO.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                itemDTO.setProductName(item.getProductName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTO.setTotalPrice(item.getTotalPrice());
                itemDTO.setNotes(item.getNotes());
                itemDTO.setWarehouseId(item.getWarehouse() != null ? item.getWarehouse().getId() : null);
                itemDTO.setCompanyId(item.getCompany() != null ? item.getCompany().getId() : null);
                return itemDTO;
            }).toList());
        }

        dto.setNotes(creditNote.getNotes());
        return dto;
    }
}
