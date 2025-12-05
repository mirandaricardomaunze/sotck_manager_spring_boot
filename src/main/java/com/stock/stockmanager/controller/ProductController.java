package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.ProductRequestDTO;
import com.stock.stockmanager.dto.ProductResponseDTO;
import com.stock.stockmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // =================== CREATE ===================
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestBody ProductRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        ProductResponseDTO created = productService.createProduct(dto);
        return ResponseEntity.ok(created);
    }

    // =================== READ ALL ===================
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestHeader("Authorization") String token) {

        List<ProductResponseDTO> products = productService.getAllProducts();
        if (products == null) {
            products = List.of(); // lista vazia em vez de null
        }
        return ResponseEntity.ok(products);
    }

    // =================== READ BY ID ===================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // =================== UPDATE ===================
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO dto,
            @RequestHeader("Authorization") String token) {

        ProductResponseDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    // =================== DELETE ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // =================== STATISTICS / REPORTS ===================

    // Total de produtos na empresa
    @GetMapping("/company/{companyId}/total")
    public ResponseEntity<Long> getTotalProductsInCompany(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String token) {

        long total = productService.getTotalProductsInCompany(companyId);
        return ResponseEntity.ok(total);
    }

    // Produtos abaixo do estoque mínimo
    @GetMapping("/company/{companyId}/below-min-stock")
    public ResponseEntity<Long> getProductsBelowMinStock(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String token) {

        long total = productService.getProductsBelowMinStock(companyId);
        return ResponseEntity.ok(total);
    }

    // Valor total de produtos na empresa
    @GetMapping("/company/{companyId}/total-value")
    public ResponseEntity<Double> getTotalValueOfProducts(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String token) {

        double total = productService.getTotalValueOfProducts(companyId);
        return ResponseEntity.ok(total);
    }

    // Produtos agrupados por categoria
    @GetMapping("/company/{companyId}/by-category")
    public ResponseEntity<Map<String, Long>> getProductsByCategory(
            @PathVariable Long companyId,
            @RequestHeader("Authorization") String token) {

        Map<String, Long> map = productService.getProductsByCategory(companyId);
        return ResponseEntity.ok(map);
    }
}
