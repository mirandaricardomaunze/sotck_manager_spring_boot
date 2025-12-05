package com.stock.stockmanager.dto;

import com.stock.stockmanager.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "É obrigatório o nome do usuário")
    private String username;

    @NotBlank(message = "É obrigatório o email")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "É obrigatório a palavra-passe")
    @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
    private String password;

    @NotNull(message = "É obrigatório informar a role")
    private Role role;

    @NotNull(message = "É obrigatório informar o ID da empresa")
    private Long companyId;

    private boolean active = true;
}
