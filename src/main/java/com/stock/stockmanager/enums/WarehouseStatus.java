package com.stock.stockmanager.enums;

/**
 * Enum representando o status de um armazém.
 */
public enum WarehouseStatus {
    ACTIVE("Ativo"),
    INACTIVE("Inativo"),
    UNDER_MAINTENANCE("Em Manutenção"),
    CLOSED("Fechado");

    private final String description;

    WarehouseStatus(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição amigável do status.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica se o status representa um armazém ativo.
     */
    public boolean isActive() {
        return this == ACTIVE;
    }

    /**
     * Converte uma string para o enum correspondente, ignorando maiúsculas/minúsculas.
     */
    public static WarehouseStatus fromString(String status) {
        for (WarehouseStatus ws : WarehouseStatus.values()) {
            if (ws.name().equalsIgnoreCase(status)) {
                return ws;
            }
        }
        throw new IllegalArgumentException("Status de armazém inválido: " + status);
    }
}
