package com.stock.stockmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String email;
    private String username;
    private String role;
    private Long companyId;
    private Long userId;
}
