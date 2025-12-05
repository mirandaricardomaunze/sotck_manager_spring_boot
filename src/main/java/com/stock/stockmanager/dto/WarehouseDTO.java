package com.stock.stockmanager.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
    private int capacity;
    private String email;
    private String phone;
    private String manager;
    private boolean active; // frontend trabalha com boolean
    private boolean principal; // indica se é o armazém default/principal
    private Long companyId;   // ID da empresa associada
    private String companyName; // Nome da empresa (para exibição)

    // Converte enum -> boolean na hora da serialização
    @JsonGetter("active")
    public boolean isActive() {
        return active;
    }

    // Converte boolean -> enum na hora da desserialização
    @JsonSetter("active")
    public void setActive(boolean active) {
        this.active = active;
    }

    // JsonGetter/Setter opcional para principal (frontend já espera boolean)
    @JsonGetter("principal")
    public boolean isPrincipal() {
        return principal;
    }

    @JsonSetter("principal")
    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}
