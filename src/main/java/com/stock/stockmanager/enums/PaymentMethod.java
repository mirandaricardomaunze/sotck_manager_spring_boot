package com.stock.stockmanager.enums;

public enum PaymentMethod {
    CASH("Dinheiro"),
    CREDIT_CARD("Cartão de Crédito"),
    DEBIT_CARD("Cartão de Débito"),
    MPESA("MPesa"),
    BANK_TRANSFER("Transferência Bancária");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static PaymentMethod fromString(String text) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(text)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pagamento não encontrado: " + text);
    }

    public static String getDescription(PaymentMethod method) {
        return method != null ? method.getDescription() : "";
    }
}
