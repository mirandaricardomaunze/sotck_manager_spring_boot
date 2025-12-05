package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.SaleRequestDTO;
import com.stock.stockmanager.dto.SaleItemRequestDTO;
import com.stock.stockmanager.dto.SaleItemResponseDTO;
import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.enums.SaleStatus;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.model.SaleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class SaleMapper {

    // ---------------- TO DTO ----------------
    public SaleResponseDTO toDTO(Sale sale) {
        return SaleResponseDTO.builder()
                .id(sale.getId())
                .saleCode(sale.getSaleCode())
                .clientName(sale.getClientName())
                .totalAmount(sale.getTotalAmount())
                .discount(sale.getDiscount())
                .amountPaid(sale.getAmountPaid())
                .change(sale.getChange())
                .paymentMethod(sale.getPaymentMethod())
                .status(sale.getStatus())
                .saleDate(sale.getSaleDate())
                .companyId(sale.getCompany() != null ? sale.getCompany().getId() : null)
                .items(
                (sale.getItems() == null ? Collections.<SaleItem>emptyList() : sale.getItems())
                        .stream()
                        .map(item -> toItemDTO(item))
                        .collect(Collectors.toList())
                )

                .build();
    }

    public SaleItemResponseDTO toItemDTO(SaleItem item) {
        return SaleItemResponseDTO.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productCode(item.getProduct().getBarcode())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    // ---------------- TO ENTITY ----------------
    public Sale toEntity(SaleRequestDTO dto, Company company) {

        Sale sale = new Sale();
        sale.setClientName(dto.getClientName());
        sale.setPaymentMethod(dto.getPaymentMethod());
        sale.setCompany(company);

        sale.setDiscount(dto.getDiscount() != null ? dto.getDiscount() : BigDecimal.ZERO);
        sale.setAmountPaid(dto.getAmountPaid() != null ? dto.getAmountPaid() : BigDecimal.ZERO);

        sale.setStatus(SaleStatus.COMPLETED);
        sale.setSaleDate(LocalDateTime.now());

        return sale;
    }

    public SaleItem toItemEntity(SaleItemRequestDTO dto, Sale sale, Product product) {
        SaleItem item = new SaleItem();
        item.setSale(sale);
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(
                dto.getUnitPrice() != null
                        ? dto.getUnitPrice()
                        : product.getSellingPrice()
        );
        return item;
    }
}
