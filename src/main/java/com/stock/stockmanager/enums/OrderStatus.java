package com.stock.stockmanager.enums;
public enum OrderStatus {
    PENDING,
    INVOICED,
    PAID,
    CANCELED,
    DRAFT,      // Criado, mas sem reservar estoque
    RESERVED,
}

