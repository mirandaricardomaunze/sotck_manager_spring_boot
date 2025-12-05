package com.stock.stockmanager.enums;

public enum Role {
    ADMIN("Administrador"),
    MANAGER("Gerente"),
    SELLER("Vendedor"),
    STOCKIST("Estoquista"),
    VIEWER("Visualizador"),
    USER("Usuário"); // Novo papel padrão

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String text) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Função de usuário não encontrada: " + text);
    }

    // Permissões
    public boolean canManageUsers() {
        return this == ADMIN || this == MANAGER;
    }

    public boolean canManageProducts() {
        return this == ADMIN || this == MANAGER || this == STOCKIST;
    }

    public boolean canProcessSales() {
        return this == ADMIN || this == MANAGER || this == SELLER;
    }

    public boolean canViewDashboard() {
        return this != null;
    }
}
