package com.stock.stockmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CompanyDTO {
    private Long id;
    @NotBlank(message = "O nome da empresa é obrigatório")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotBlank(message = "O endereço é obrigatório")
    private String address;

    @NotBlank(message = "O número de telefone é obrigatório")
    @Pattern(regexp = "^[0-9()+\\-\\s]*$", message = "Telefone inválido")
    private String phoneNumber;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String website;

    @NotBlank(message = "O NIF/Tax ID é obrigatório")
    @Size(min = 5, max = 20, message = "Tax ID deve ter entre 5 e 20 caracteres")
    private String taxId;

    private String registrationNumber;

    private String logoUrl;

    @Size(max = 500, message = "Descrição não pode ultrapassar 500 caracteres")
    private String description;

    @NotBlank(message = "O país é obrigatório")
    private String country;

    @NotBlank(message = "A cidade é obrigatória")
    private String city;

    private String postalCode;

    private String industry;

    @Email(message = "Email de contato inválido")
    private String contactEmail;

    @Pattern(regexp = "^[0-9()+\\-\\s]*$", message = "Telefone de contato inválido")
    private String contactPhone;

}
