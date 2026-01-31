package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.WarehouseRequestDTO;
import com.stock.stockmanager.dto.WarehouseResponseDTO;
import com.stock.stockmanager.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(
            @RequestBody WarehouseRequestDTO requestDTO) {

        return ResponseEntity.ok(
                warehouseService.createWarehouse(requestDTO)
        );
    }

    // ================= READ =================

    /** 🔥 USAR NO COMBOBOX */
    @GetMapping("/company/{companyId}/active")
    public ResponseEntity<List<WarehouseResponseDTO>> getActiveWarehousesByCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                warehouseService.getActiveWarehousesByCompany(companyId)
        );
    }

    /** TODOS os armazéns da empresa */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehousesByCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                warehouseService.getAllWarehousesByCompany(companyId)
        );
    }

    /** Armazém principal */
    @GetMapping("/company/{companyId}/principal")
    public ResponseEntity<WarehouseResponseDTO> getPrincipalWarehouseByCompany(
            @PathVariable Long companyId) {

        return ResponseEntity.ok(
                warehouseService.getPrincipalWarehouseByCompany(companyId)
        );
    }

    /** Por ID */
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                warehouseService.getWarehouseById(id)
        );
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseRequestDTO requestDTO) {

        return ResponseEntity.ok(
                warehouseService.updateWarehouse(id, requestDTO)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    // ================= PRINCIPAL =================
    @PostMapping("/{id}/set-principal")
    public ResponseEntity<WarehouseResponseDTO> setPrincipalWarehouse(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                warehouseService.setPrincipalWarehouse(id)
        );
    }
}
