package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.WarehouseRequestDTO;
import com.stock.stockmanager.dto.WarehouseResponseDTO;
import com.stock.stockmanager.enums.WarehouseStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.DuplicateResourceException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // ================= CREATE =================
    public WarehouseResponseDTO createWarehouse(WarehouseRequestDTO dto) {
        validateWarehouseDTO(dto);

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            warehouseRepository.findByEmail(dto.getEmail())
                    .ifPresent(w -> {
                        throw new DuplicateResourceException(
                                "Já existe um armazém com esse email");
                    });
        }

        Warehouse warehouse = dtoToEntity(dto);

        if (dto.isPrincipal()) {
            unsetPrincipalByCompany(warehouse.getCompany().getId());
            warehouse.setPrincipal(true);
        }

        return entityToResponseDTO(warehouseRepository.save(warehouse));
    }

    // ================= READ =================
    public WarehouseResponseDTO getWarehouseById(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse não encontrado"));
        return entityToResponseDTO(warehouse);
    }

    /** Somente armazéns ativos (ComboBox / POS) */
    public List<WarehouseResponseDTO> getActiveWarehousesByCompany(Long companyId) {
        return warehouseRepository
                .findByCompanyIdAndStatus(companyId, WarehouseStatus.ACTIVE)
                .stream()
                .map(this::entityToResponseDTO)
                .toList();
    }

    /** Armazém principal */
    public WarehouseResponseDTO getPrincipalWarehouseByCompany(Long companyId) {
        return warehouseRepository
                .findByCompanyIdAndPrincipalTrue(companyId)
                .stream()
                .findFirst()
                .map(this::entityToResponseDTO)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Nenhum armazém principal encontrado"));
    }

    /** Todos os armazéns da empresa */
    public List<WarehouseResponseDTO> getAllWarehousesByCompany(Long companyId) {
        return warehouseRepository.findByCompanyId(companyId)
                .stream()
                .map(this::entityToResponseDTO)
                .toList();
    }

    // ================= UPDATE =================
    public WarehouseResponseDTO updateWarehouse(Long id, WarehouseRequestDTO dto) {
        validateWarehouseDTO(dto);

        Warehouse existing = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse não encontrado"));

        updateEntity(existing, dto);

        // Atualiza empresa se mudou
        if (!existing.getCompany().getId().equals(dto.getCompanyId())) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Empresa não encontrada"));
            existing.setCompany(company);
        }

        if (dto.isPrincipal()) {
            unsetPrincipalByCompany(existing.getCompany().getId());
            existing.setPrincipal(true);
        } else {
            existing.setPrincipal(false);
        }

        return entityToResponseDTO(warehouseRepository.save(existing));
    }

    // ================= DELETE =================
    public void deleteWarehouse(Long id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse não encontrado"));
        warehouseRepository.delete(warehouse);
    }

    // ================= PRINCIPAL =================
    public WarehouseResponseDTO setPrincipalWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Warehouse não encontrado"));

        unsetPrincipalByCompany(warehouse.getCompany().getId());
        warehouse.setPrincipal(true);

        return entityToResponseDTO(warehouseRepository.save(warehouse));
    }

    // ================= HELPERS =================
    private void unsetPrincipalByCompany(Long companyId) {
        warehouseRepository.findByCompanyIdAndPrincipalTrue(companyId)
                .forEach(w -> {
                    w.setPrincipal(false);
                    warehouseRepository.save(w);
                });
    }

    private void validateWarehouseDTO(WarehouseRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
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
        updateEntity(warehouse, dto);

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Empresa não encontrada"));

        warehouse.setCompany(company);
        warehouse.setPrincipal(dto.isPrincipal());

        return warehouse;
    }

    private void updateEntity(Warehouse warehouse, WarehouseRequestDTO dto) {
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setDescription(dto.getDescription());
        warehouse.setCapacity(dto.getCapacity());
        warehouse.setEmail(dto.getEmail());
        warehouse.setPhone(dto.getPhone());
        warehouse.setManager(dto.getManager());
        warehouse.setStatus(
                dto.isActive() ? WarehouseStatus.ACTIVE : WarehouseStatus.INACTIVE
        );
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
}
