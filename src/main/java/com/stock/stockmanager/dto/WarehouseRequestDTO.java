package com.stock.stockmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseRequestDTO {
    private String name;
    private String location;
    private String description;
    private int capacity;
    private String email;
    private String phone;
    private String manager;
    private boolean active;      // frontend envia boolean
    private boolean principal;   // se será o armazém
    private Long companyId;      // ‘ID’ da empresa associada
}
