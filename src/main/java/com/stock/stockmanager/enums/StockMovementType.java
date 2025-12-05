package com.stock.stockmanager.enums;
public enum StockMovementType {
    ENTRY("Entrada"),
    EXIT("Saída"),
    ADJUSTMENT("Ajuste"),
    RETURN("Devolução"),
    LOSS("Perda");

    private final String description;

    StockMovementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPositive() {
        return this == ENTRY || this == RETURN;
    }

    public boolean isNegative() {
        return this == EXIT || this == LOSS;
    }
}
