package com.stock.stockmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SupplierDTO {
    private Long id;

    @NotBlank(message = "O nome do fornecedor é obrigatório")
    private String name;
    @Email(message = "E-mail inválido")
    private String email;

    @Size(min = 7, max = 15, message = "O telefone deve ter entre 7 e 15 caracteres")
    private String phone;

    private String nuit;
    private String address;
    private String website;
    private String notes;
    private Long companyId;
    private String company;
}
