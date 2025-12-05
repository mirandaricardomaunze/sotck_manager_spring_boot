package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.MonthlyMovementDTO;
import com.stock.stockmanager.dto.SaleRequestDTO;
import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SaleController {

    private final SaleService saleService;

    // ---------------------------------------------------------------
    // Registrar venda
    // ---------------------------------------------------------------
    @PostMapping
    public ResponseEntity<SaleResponseDTO> register(@RequestBody SaleRequestDTO dto) {
        log.info("API - Registrando venda");
        return ResponseEntity.ok(saleService.registerSale(dto));
    }
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotal(@RequestParam String period) {
        BigDecimal total = saleService.getTotalByPeriod(period);
        return ResponseEntity.ok(total);
    }


    @GetMapping("/profit")
    public BigDecimal getProfit(@RequestParam String period) {
        return saleService.getProfitByPeriod(period);
    }
    @GetMapping("/movement")
    public ResponseEntity<List<MonthlyMovementDTO>> getMonthlyMovement(
            @RequestParam Long companyId,
            @RequestParam String period) {

        List<MonthlyMovementDTO> movements = saleService.getMonthlyMovement(companyId, period);
        return ResponseEntity.ok(movements);
    }


    // ---------------------------------------------------------------
    // Buscar venda por ID
    // ---------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    // ---------------------------------------------------------------
    // Listar vendas paginadas
    // ---------------------------------------------------------------
    @GetMapping
    public ResponseEntity<Page<SaleResponseDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(saleService.listAll(pageable));
    }

    // ---------------------------------------------------------------
    // Deletar venda
    // ---------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
