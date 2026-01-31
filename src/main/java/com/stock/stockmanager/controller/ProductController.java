package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.ProductRequestDTO;
import com.stock.stockmanager.dto.ProductResponseDTO;
import com.stock.stockmanager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO created = productService.createProduct(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =================== READ ALL ===================
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {

        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // =================== READ BY ID ===================
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Long id) {

        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    // =================== UPDATE ===================
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO updated = productService.updateProduct(id, dto);
        return ResponseEntity.ok(updated);
    }

    // =================== DELETE ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // =================== STATISTICS / REPORTS ===================

    // Total de produtos por empresa
    @GetMapping("/company/{companyId}/total")
    public ResponseEntity<Long> getTotalProductsInCompany(
            @PathVariable Long companyId) {

        long total = productService.getTotalProductsInCompany(companyId);
        return ResponseEntity.ok(total);
    }

    // Produtos abaixo do estoque mínimo
    @GetMapping("/company/{companyId}/below-min-stock")
    public ResponseEntity<Long> getProductsBelowMinStock(
            @PathVariable Long companyId) {

        long total = productService.getProductsBelowMinStock(companyId);
        return ResponseEntity.ok(total);
    }

    // Valor total do estoque (preço de venda)
    @GetMapping("/company/{companyId}/total-value")
    public ResponseEntity<Double> getTotalValueOfProducts(
            @PathVariable Long companyId) {

        double total = productService.getTotalValueOfProducts(companyId);
        return ResponseEntity.ok(total);
    }

    // Produtos agrupados por categoria (Dashboard / Gráficos)
    @GetMapping("/company/{companyId}/by-category")
    public ResponseEntity<Map<String, Long>> getProductsByCategory(
            @PathVariable Long companyId) {

        Map<String, Long> result = productService.getProductsByCategory(companyId);
        return ResponseEntity.ok(result);
    }
}
