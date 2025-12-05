package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.WarehouseRequestDTO;
import com.stock.stockmanager.dto.WarehouseResponseDTO;
import com.stock.stockmanager.enums.WarehouseStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.DuplicateResourceException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final CompanyRepository companyRepository;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            CompanyRepository companyRepository) {
        this.warehouseRepository = warehouseRepository;
        this.companyRepository = companyRepository;
    }

    // ===== CREATE =====
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO requestDTO) {
        validateWarehouseDTO(requestDTO);

        // Verifica email duplicado
        if (requestDTO.getEmail() != null && !requestDTO.getEmail().isEmpty()) {
            Optional<Warehouse> existing = warehouseRepository.findByEmail(requestDTO.getEmail());
            if (existing.isPresent()) {
                throw new DuplicateResourceException("Já existe um warehouse com esse email");
            }
        }

        Warehouse warehouse = dtoToEntity(requestDTO);
        warehouse.setStatus(requestDTO.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE);

        // Se for marcado como principal, desmarcar outros da empresa
        if (requestDTO.isPrincipal()) {
            unsetPrincipalByCompany(warehouse.getCompany().getId());
            warehouse.setPrincipal(true);
        }

        warehouse = warehouseRepository.save(warehouse);
        return entityToResponseDTO(warehouse);
    }

    // ===== READ ALL =====
    public List<WarehouseResponseDTO> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::entityToResponseDTO)
                .collect(Collectors.toList());
    }

    // ===== READ BY ID =====
    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse não encontrado"));
        return entityToResponseDTO(warehouse);
    }

    // ===== READ BY COMPANY ID =====
    public List<WarehouseResponseDTO> getWarehousesByCompany(Long companyId) {
        return warehouseRepository.findByCompanyIdAndPrincipalTrue(companyId)
                .stream()
                .map(this::entityToResponseDTO)
                .toList();
    }

    public List<WarehouseResponseDTO> getAllWarehousesByCompany(Long companyId) {
        return warehouseRepository.findByCompanyId(companyId)
                .stream()
                .map(this::entityToResponseDTO)
                .toList();
    }

    // ===== UPDATE =====
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO requestDTO) {
        validateWarehouseDTO(requestDTO);

        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse não encontrado"));

        existing.setName(requestDTO.getName());
        existing.setLocation(requestDTO.getLocation());
        existing.setDescription(requestDTO.getDescription());
        existing.setCapacity(requestDTO.getCapacity());
        existing.setEmail(requestDTO.getEmail());
        existing.setPhone(requestDTO.getPhone());
        existing.setManager(requestDTO.getManager());
        existing.setStatus(requestDTO.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE);

        // Atualiza empresa
        if (requestDTO.getCompanyId() != null) {
            Company company = companyRepository.findById(requestDTO.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
            existing.setCompany(company);
        }

        // Se for principal, desmarca os outros
        if (requestDTO.isPrincipal()) {
            unsetPrincipalByCompany(existing.getCompany().getId());
            existing.setPrincipal(true);
        } else {
            existing.setPrincipal(false);
        }

        Warehouse updated = warehouseRepository.save(existing);
        return entityToResponseDTO(updated);
    }

    // ===== DELETE =====
    public void deleteWarehouse(Long id) {
        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse não encontrado"));
        warehouseRepository.delete(existing);
    }

    // ===== SET PRINCIPAL =====
    public WarehouseResponseDTO setPrincipalWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse não encontrado"));

        unsetPrincipalByCompany(warehouse.getCompany().getId());
        warehouse.setPrincipal(true);
        Warehouse updated = warehouseRepository.save(warehouse);

        return entityToResponseDTO(updated);
    }

    // ===== HELPER METHODS =====
    private void validateWarehouseDTO(WarehouseRequestDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }
        if (dto.getCapacity() <= 0) {
            throw new BusinessException("Capacidade deve ser maior que zero");
        }
        if (dto.getCompanyId() == null) {
            throw new BusinessException("CompanyId é obrigatório");
        }
    }

    private Warehouse dtoToEntity(WarehouseRequestDTO dto) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setDescription(dto.getDescription());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setEmail(dto.getEmail());
        warehouse.setPhone(dto.getPhone());
        warehouse.setManager(dto.getManager());
        warehouse.setStatus(dto.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE);

        // Associa empresa
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        warehouse.setCompany(company);

        warehouse.setPrincipal(dto.isPrincipal());

        return warehouse;
    }

    private WarehouseResponseDTO entityToResponseDTO(Warehouse warehouse) {
        WarehouseResponseDTO dto = new WarehouseResponseDTO();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setDescription(warehouse.getDescription());
        dto.setCapacity(warehouse.getCapacity());
        dto.setEmail(warehouse.getEmail());
        dto.setPhone(warehouse.getPhone());
        dto.setManager(warehouse.getManager());
        dto.setActive(warehouse.getStatus() == WarehouseStatus.ACTIVE);
        dto.setPrincipal(warehouse.isPrincipal());

        if (warehouse.getCompany() != null) {
            dto.setCompanyId(warehouse.getCompany().getId());
            dto.setCompanyName(warehouse.getCompany().getName());
        }

        return dto;
    }

    private void unsetPrincipalByCompany(Long companyId) {
        List<Warehouse> warehouses = warehouseRepository.findByCompanyIdAndPrincipalTrue(companyId);
        for (Warehouse w : warehouses) {
            w.setPrincipal(false);
            warehouseRepository.save(w);
        }
    }
}
