package com.stock.stockmanager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseResponseDTO {
    private Long id;
    private String name;
    private String location;
    private String description;
    private int capacity;
    private String email;
    private String phone;
    private String manager;
    private boolean active;     // status convertido para boolean
    private boolean principal;  // armazém default
    private Long companyId;
    private String companyName;
}
