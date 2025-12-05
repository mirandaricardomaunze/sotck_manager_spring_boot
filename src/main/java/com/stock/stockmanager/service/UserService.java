package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.UserDTO;
import com.stock.stockmanager.dto.UserResponseDTO;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.User;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.UserRepository;
import com.stock.stockmanager.repository.WarehouseRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final WarehouseRepository warehouseRepository;

    public UserService(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            WarehouseRepository warehouseRepository
    ) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.warehouseRepository = warehouseRepository;
    }

    public UserResponseDTO createUser(UserDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setActive(dto.isActive());
        user.setCompany(company);

        user = userRepository.save(user);

        return mapToResponseDTO(user);
    }
    public UserResponseDTO findByEmail(String email) {
        return userRepository.findByEmail(email)       // busca pelo endereço eletrónico
                .map(this::mapToResponseDTO)           // converte para DTO
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")); // se não achar
    }
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return mapToResponseDTO(user);
    }

    public UserResponseDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        user.setActive(dto.isActive());
        user.setCompany(company);

        user = userRepository.save(user);

        return mapToResponseDTO(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive(),
                user.getCompany().getId()
        );
    }
}
