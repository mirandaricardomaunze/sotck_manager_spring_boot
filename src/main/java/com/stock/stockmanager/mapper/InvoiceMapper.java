package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.InvoiceDTO;
import com.stock.stockmanager.dto.InvoiceItemDTO;
import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.model.Invoice;
import com.stock.stockmanager.model.InvoiceItem;
import com.stock.stockmanager.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class InvoiceMapper {

    // ============================
    // ORDER → INVOICE
    // ============================
    public static Invoice fromOrder(Order order) {
        if (order == null) return null;

        Invoice invoice = new Invoice();

        // Dados básicos da fatura
        invoice.setInvoiceNumber(order.getOrderNumber());
        invoice.setInvoiceDate(order.getOrderDate());
        invoice.setStatus(InvoiceStatus.PENDING);

        invoice.setTotalAmount(
                order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO
        );

        // Dados do cliente
        invoice.setCustomerName(order.getCustomerName());
        invoice.setDeliveryAddress(order.getDeliveryAddress());
        invoice.setCustomerEmail(order.getCustomerEmail());
        invoice.setCustomerContact(order.getCustomerContact());
        invoice.setCustomerNuit(order.getCustomerNuit());
        invoice.setPaymentMethod(order.getPaymentMethod());

        // Empresa / Armazém
        invoice.setCompanyName(order.getCompanyName());
        invoice.setWarehouseName(order.getWarehouseName());

        // Referenciar o pedido
        invoice.setOrder(order);

        // Itens da fatura
        if (order.getItems() != null) {
            List<InvoiceItem> items = order.getItems()
                    .stream()
                    .map(orderItem -> {
                        InvoiceItem item = new InvoiceItem();
                        item.setInvoice(invoice);
                        item.setProduct(orderItem.getProduct());
                        item.setQuantity(orderItem.getQuantity());

                        item.setUnitPrice(
                                orderItem.getUnitPrice() != null ? orderItem.getUnitPrice() : BigDecimal.ZERO
                        );

                        item.setTotalPrice(
                                orderItem.getTotalPrice() != null ? orderItem.getTotalPrice() : BigDecimal.ZERO
                        );

                        // Nome do produto
                        item.setProductName(
                                orderItem.getProduct() != null ? orderItem.getProduct().getName() : ""
                        );

                        // Notas
                        item.setNotes(
                                orderItem.getNotes() != null ? orderItem.getNotes() : ""
                        );

                        return item;
                    })
                    .collect(Collectors.toList());

            invoice.setItems(items);
        }

        invoice.setNotes(order.getNotes());

        return invoice;
    }

    // ============================
    // INVOICE → DTO
    // ============================
    public static InvoiceDTO toDTO(Invoice invoice) {
        if (invoice == null) return null;

        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus() != null ? invoice.getStatus().name() : null);

        // Cliente
        dto.setCustomerName(invoice.getCustomerName());
        dto.setDeliveryAddress(invoice.getDeliveryAddress());
        dto.setCustomerEmail(invoice.getCustomerEmail());
        dto.setCustomerContact(invoice.getCustomerContact());
        dto.setCustomerNuit(invoice.getCustomerNuit());
        dto.setPaymentMethod(invoice.getPaymentMethod());

        // Empresa / Armazém
        dto.setCompanyName(invoice.getCompanyName());
        dto.setWarehouseName(invoice.getWarehouseName());

        // ID do pedido
        dto.setOrderId(invoice.getOrder() != null ? invoice.getOrder().getId() : null);

        // Itens
        if (invoice.getItems() != null) {
            List<InvoiceItemDTO> items = invoice.getItems()
                    .stream()
                    .map(InvoiceMapper::toItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }

        dto.setNotes(invoice.getNotes());

        return dto;
    }

    // ============================
    // INVOICE ITEM → DTO
    // ============================
    private static InvoiceItemDTO toItemDTO(InvoiceItem item) {
        if (item == null) return null;

        InvoiceItemDTO dto = new InvoiceItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setProductName(item.getProductName());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setNotes(item.getNotes());

        return dto;
    }
}
