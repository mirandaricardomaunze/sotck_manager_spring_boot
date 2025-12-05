package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.StockDTO;
import com.stock.stockmanager.dto.StockRequestDTO;
import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    // ===============================================
    // LISTA TODO O STOCK
    // ===============================================
    @GetMapping
    public ResponseEntity<List<StockDTO>> getAll() {
        return ResponseEntity.ok(stockService.getAll());
    }

    @PostMapping
    public ResponseEntity<StockDTO> createOrUpdate(@RequestBody StockRequestDTO dto) {
        return ResponseEntity.ok(stockService.createOrUpdate(dto));
    }

    // ===============================================
    // STOCK POR ARMAZÉM
    // ===============================================
    @GetMapping("/warehouse/{id}")
    public ResponseEntity<List<StockDTO>> getAllByWarehouse(@PathVariable("id") Long warehouseId) {
        return ResponseEntity.ok(stockService.getAllByWarehouse(warehouseId));
    }

    // ===============================================
    // STOCK POR PRODUTO
    // ===============================================
    @GetMapping("/product/{id}")
    public ResponseEntity<List<StockDTO>> getAllByProduct(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(stockService.getAllByProduct(productId));
    }

    // ===============================================
    // TOTAL STOCK POR PRODUTO
    // ===============================================
    @GetMapping("/total/product/{id}")
    public ResponseEntity<Long> getTotalByProduct(@PathVariable("id") Long productId) {
        return ResponseEntity.ok(stockService.getTotalStockByProduct(productId));
    }

    // ===============================================
    // TOTAL STOCK POR ARMAZÉM
    // ===============================================
    @GetMapping("/total/warehouse/{id}")
    public ResponseEntity<Long> getTotalByWarehouse(@PathVariable("id") Long warehouseId) {
        return ResponseEntity.ok(stockService.getTotalStockByWarehouse(warehouseId));
    }

    // ===============================================
    // SUMÁRIO AGRUPADO POR PRODUTO
    // ===============================================
    @GetMapping("/summary/product")
    public ResponseEntity<List<StockSummaryDTO>> getSummaryByProduct() {
        return ResponseEntity.ok(stockService.getSummaryByProduct());
    }

    // ===============================================
    // SUMÁRIO AGRUPADO POR ARMAZÉM
    // ===============================================
    @GetMapping("/summary/warehouse")
    public ResponseEntity<List<StockSummaryDTO>> getSummaryByWarehouse() {
        return ResponseEntity.ok(stockService.getSummaryByWarehouse());
    }

}
