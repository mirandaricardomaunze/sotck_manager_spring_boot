package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.OrderDTO;
import com.stock.stockmanager.dto.OrderItemDTO;
import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.enums.OrderStatus;
import com.stock.stockmanager.model.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    /** 🔹 Converte Order → OrderDTO */
    public static OrderDTO toDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCompanyId(order.getCompany() != null ? order.getCompany().getId() : null);
        dto.setCompanyName(order.getCompanyName());
        dto.setWarehouseId(order.getWarehouse() != null ? order.getWarehouse().getId() : null);
        dto.setWarehouseName(order.getWarehouseName());
        dto.setOrderDate(order.getOrderDate());
        dto.setCustomerName(order.getCustomerName());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerContact(order.getCustomerContact());
        dto.setDeliveryAddress(order.getDeliveryAddress());
        dto.setCustomerNuit(order.getCustomerNuit());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNotes(order.getNotes());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : OrderStatus.PENDING.name());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setItems(order.getItems() != null
                ? order.getItems().stream().map(OrderMapper::toItemDTO).collect(Collectors.toList())
                : Collections.emptyList());

        return dto;
    }

    /** 🔹 Converte OrderDTO → Order, com validações */
    public static Order toEntity(
            OrderDTO dto,
            Company company,
            Warehouse warehouse,
            List<Product> allProducts) {

        if (dto == null) {
            throw new IllegalArgumentException("O DTO do pedido não pode ser nulo");
        }

        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("O nome do cliente é obrigatório");
        }

        if (dto.getDeliveryAddress() == null || dto.getDeliveryAddress().isBlank()) {
            throw new IllegalArgumentException("O endereço de entrega é obrigatório");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item");
        }

        Order order = new Order();
        order.setId(dto.getId());
        order.setOrderDate(dto.getOrderDate());
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());
        order.setCustomerContact(dto.getCustomerContact());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setCustomerNuit(dto.getCustomerNuit());
        order.setOrderNumber(dto.getOrderNumber());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setNotes(dto.getNotes());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(dto.getTotalAmount());
        order.setCompany(company);
        order.setWarehouse(warehouse);
        order.setCompanyName(company != null ? company.getName() : null);
        order.setWarehouseName(warehouse != null ? warehouse.getName() : null);

        // 🔹 Mapeia os itens do pedido
        order.setItems(dto.getItems().stream()
                .map(itemDTO -> toItemEntity(itemDTO, order, allProducts))
                .collect(Collectors.toList()));

        return order;
    }

    /** 🔹 Converte OrderItem → OrderItemDTO */
    public static OrderItemDTO toItemDTO(OrderItem item) {
        if (item == null) return null;

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        dto.setProductName(item.getProductName()); // ✅ Agora vem direto da entidade
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    /** 🔹 Converte OrderItemDTO → OrderItem e associa ao Order */
    public static OrderItem toItemEntity(OrderItemDTO dto, Order order, List<Product> allProducts) {
        if (dto == null) {
            throw new IllegalArgumentException("O item do pedido não pode ser nulo");
        }

        if (dto.getProductId() == null) {
            throw new IllegalArgumentException("O item deve conter um produto válido");
        }

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }

        OrderItem item = new OrderItem();
        item.setId(dto.getId());
        item.setOrder(order);

        // 🔹 Associa o produto real
        Product product = allProducts.stream()
                .filter(p -> p.getId().equals(dto.getProductId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado para o item"));

        item.setProduct(product);

        // 🔹 Define o nome do produto (fixado no momento do pedido)
        item.setProductName(dto.getProductName() != null ? dto.getProductName() : product.getName());

        // 🔹 Define o preço e total
        BigDecimal unitPrice = dto.getUnitPrice() != null
                ? dto.getUnitPrice()
                : product.getSellingPrice();

        item.setUnitPrice(unitPrice);
        item.setQuantity(dto.getQuantity());
        item.calculateTotalPrice();

        return item;
    }
}
