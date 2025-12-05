package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.MovementRequestDTO;
import com.stock.stockmanager.dto.MovementResponseDTO;
import com.stock.stockmanager.service.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService service;

    /** Cria movimento */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovementResponseDTO create(@Valid @RequestBody MovementRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/filter")
    public List<MovementResponseDTO> filterByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return service.filterByDate(start, end);
    }

    /** Lista todos os movimentos */
    @GetMapping
    public List<MovementResponseDTO> getAll() {
        return service.getAll();
    }

    /** Busca movimento por ‘ID’ */
    @GetMapping("/{id}")
    public MovementResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    /** Atualiza movimento existente */
    @PutMapping("/{id}")
    public MovementResponseDTO update(@PathVariable Long id, @Valid @RequestBody MovementRequestDTO dto) {
        return service.update(id, dto);
    }

    /** Deleta movimento */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
