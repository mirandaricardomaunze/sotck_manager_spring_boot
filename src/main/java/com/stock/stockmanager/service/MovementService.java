package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.MovementRequestDTO;
import com.stock.stockmanager.dto.MovementResponseDTO;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.MovementMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovementService {

    private final MovementRepository movementRepository;
    private final CompanyRepository companyRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final MovementMapper mapper;

    // Cria um movimento
    public MovementResponseDTO create(MovementRequestDTO dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Movement movement = mapper.toEntity(dto, company, warehouse, product, user);

        movementRepository.save(movement);
        return mapper.toDTO(movement);
    }

    // Busca por ID
    public MovementResponseDTO getById(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));
        return mapper.toDTO(movement);
    }

    // Lista todos
    public List<MovementResponseDTO> getAll() {
        return movementRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    // FILTRAR POR DATA (CORRIGIDO)
    public List<MovementResponseDTO> filterByDate(LocalDateTime start, LocalDateTime end) {
        List<Movement> movements = movementRepository.findByDateBetween(start, end);
        return movements.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
    // Atualiza
    public MovementResponseDTO update(Long id, MovementRequestDTO dto) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado"));

        movement.setDescription(dto.getDescription());
        movement.setType(dto.getType());
        movement.setOrigin(dto.getOrigin());
        movement.setStatus(dto.getStatus());
        movement.setQuantity(dto.getQuantity());
        movement.setReferenceNumber(dto.getReferenceNumber());
        movement.setCompany(company);
        movement.setWarehouse(warehouse);
        movement.setProduct(product);
        movement.setUser(user);

        movementRepository.save(movement);
        return mapper.toDTO(movement);
    }

    // Deleta
    public void delete(Long id) {
        if (!movementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movimento não encontrado");
        }
        movementRepository.deleteById(id);
    }
}
