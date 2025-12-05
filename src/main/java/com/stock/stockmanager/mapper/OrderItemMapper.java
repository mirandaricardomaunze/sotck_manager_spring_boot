package com.stock.stockmanager.mapper;
import com.stock.stockmanager.dto.OrderItemDTO;
import com.stock.stockmanager.model.OrderItem;
import com.stock.stockmanager.model.Product;



public class OrderItemMapper {
    public static OrderItemDTO toDTO(OrderItem entity) {
        if (entity == null) return null;
        return OrderItemDTO.builder()
                .id(entity.getId())
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .productName(entity.getProductName()) // ✅ Agora vem diretamente do campo da entidade
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

    // 🔹 Converter DTO → Entidade
    public static OrderItem toEntity(OrderItemDTO dto, Product product) {
        if (dto == null) return null;
        OrderItem entity = new OrderItem();
        entity.setId(dto.getId());
        entity.setProduct(product); // relação JPA
        entity.setProductName(dto.getProductName() != null ? dto.getProductName() : product.getName());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.calculateTotalPrice();

        return entity;
    }
}

