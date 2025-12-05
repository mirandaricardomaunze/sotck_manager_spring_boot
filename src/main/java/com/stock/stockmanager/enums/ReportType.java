package com.stock.stockmanager.enums;

public enum ReportType {
    DAILY_SALES("Vendas Diárias"),
    MONTHLY_SALES("Vendas Mensais"),
    PRODUCT_PERFORMANCE("Desempenho de Produtos"),
    STOCK_ALERTS("Alertas de Estoque"),
    FINANCIAL_SUMMARY("Resumo Financeiro"),
    PAYMENT_METHODS("Métodos de Pagamento");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static ReportType fromString(String text) {
        for (ReportType type : ReportType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Tipo de relatório não encontrado: " + text);
    }
}
