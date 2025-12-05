package com.stock.stockmanager.util;

public  final class ProductQuantity {
    private final Long quantity;

    public ProductQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        throw new UnsupportedOperationException("Cannot modify quantity");
    }
}
