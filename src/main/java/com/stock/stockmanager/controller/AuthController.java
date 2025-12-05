package com.stock.stockmanager.controller;

import com.stock.stockmanager.config.JwtService;
import com.stock.stockmanager.dto.LoginResponseDTO;
import com.stock.stockmanager.dto.UserDTO;
import com.stock.stockmanager.dto.UserResponseDTO;
import com.stock.stockmanager.enums.Role;
import com.stock.stockmanager.model.User;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.UserRepository;
import com.stock.stockmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtService jwtService;
    @Autowired private CompanyRepository  companyRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody UserDTO dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole() != null ? Role.valueOf((String.valueOf(dto.getRole()))) :  Role.USER);
        user.setActive(true);

        if (dto.getCompanyId() != null) {
            user.setCompany(companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Empresa não encontrada")));
        } else {
            throw new RuntimeException("companyId é obrigatório!");
        }

        userRepository.save(user);
        return "Usuário registrado com sucesso!";
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody UserDTO dto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        // gerar token
        String token = jwtService.generateToken(dto.getEmail());

        // buscar utilizador completo (para pegar username)
        UserResponseDTO user = userService.findByEmail(dto.getEmail());

        // retornar token + dados do usuário
        return new LoginResponseDTO(
                token,
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getCompanyId(),
                user.getId());
    }

    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

}
