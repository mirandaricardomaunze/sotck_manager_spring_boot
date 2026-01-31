package com.stock.stockmanager.enums;

public enum SaleStatus {
    PENDING("Pendente"),
    COMPLETED("Concluída"),
    CANCELED("Cancelada");

    private final String description;

    SaleStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
