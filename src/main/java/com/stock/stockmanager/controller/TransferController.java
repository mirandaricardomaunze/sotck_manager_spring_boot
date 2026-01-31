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
    public ResponseEntity<TransferResponseDTO> create(
            @RequestBody TransferRequestDTO dto,
            @RequestHeader("userId") Long userId // Pega o userId do header
    ) {
        // Passa o userId diretamente para o service
        TransferResponseDTO response = transferService.createTransfer(dto, userId);

        return ResponseEntity
                .created(URI.create("/api/transfers/" + response.getId()))
                .body(response);
    }

    // ===============================================
    // GET BY ID
    // ===============================================
    @GetMapping("/{id}")
    public ResponseEntity<TransferResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transferService.getById(id));
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
    public ResponseEntity<TransferResponseDTO> update(
            @PathVariable Long id,
            @RequestBody TransferRequestDTO dto,
            @RequestHeader("userId") Long userId // Pode ser usado se quiser validar permissões
    ) {
        TransferResponseDTO response = transferService.updateTransfer(id, dto);
        return ResponseEntity.ok(response);
    }

    // ===============================================
    // DELETE
    // ===============================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestHeader("userId") Long userId // Pode ser usado se quiser validar permissões
    ) {
        transferService.deleteTransfer(id);
        return ResponseEntity.noContent().build();
    }
}
