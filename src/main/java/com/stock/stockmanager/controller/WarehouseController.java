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

    // ===== CREATE =====
    @PostMapping
    public ResponseEntity<WarehouseResponseDTO> createWarehouse(@RequestBody WarehouseRequestDTO requestDTO) {
        WarehouseResponseDTO created = warehouseService.createWarehouse(requestDTO);
        return ResponseEntity.ok(created);
    }

    // ===== READ ALL =====
    @GetMapping
    public ResponseEntity<List<WarehouseResponseDTO>> getAllWarehouses() {
        List<WarehouseResponseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<WarehouseResponseDTO>> getWarehousesByCompany(@PathVariable Long companyId) {
        List<WarehouseResponseDTO> warehouses = warehouseService.getWarehousesByCompany(companyId);
        return ResponseEntity.ok(warehouses);
    }

    // ===== READ BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> getWarehouseById(@PathVariable Long id) {
        WarehouseResponseDTO warehouse = warehouseService.getWarehouseById(id);
        return ResponseEntity.ok(warehouse);
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponseDTO> updateWarehouse(
            @PathVariable Long id,
            @RequestBody WarehouseRequestDTO requestDTO) {
        WarehouseResponseDTO updated = warehouseService.updateWarehouse(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }

    // ===== SET PRINCIPAL =====
    @PostMapping("/{id}/set-principal")
    public ResponseEntity<WarehouseResponseDTO> setPrincipal(@PathVariable Long id) {
        WarehouseResponseDTO updated = warehouseService.setPrincipalWarehouse(id);
        return ResponseEntity.ok(updated);
    }
}
