package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.TransferRequestDTO;
import com.stock.stockmanager.dto.TransferResponseDTO;
import com.stock.stockmanager.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    // ===============================================
// CREATE
// ===============================================
    @PostMapping
    public ResponseEntity<TransferResponseDTO> create(@RequestBody TransferRequestDTO dto) {
        TransferResponseDTO response = transferService.createTransfer(dto);
        return ResponseEntity
                .created(URI.create("/api/transfers/" + response.getId()))
                .body(response);
    }

    // ===============================================
// GET BY ID
// ===============================================
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponseDTO> getById(@PathVariable Long id) {
        TransferResponseDTO dto = transferService.getById(id);
        return ResponseEntity.ok(dto);
    }

    // ===============================================
// LIST ALL
// ===============================================
    @GetMapping
    public ResponseEntity<List<TransferResponseDTO>> getAll() {
        return ResponseEntity.ok(transferService.getAll());
    }

    // ===============================================
// UPDATE
// ===============================================
    @PutMapping("/{id}")
    public ResponseEntity<TransferResponseDTO> update(@PathVariable Long id,
                                                      @RequestBody TransferRequestDTO dto) {
        TransferResponseDTO updated = transferService.updateTransfer(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ===============================================
// DELETE
// ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }

}
