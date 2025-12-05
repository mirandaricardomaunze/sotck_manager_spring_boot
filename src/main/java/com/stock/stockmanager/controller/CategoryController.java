package com.stock.stockmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.stockmanager.dto.CategoryDTO;
import com.stock.stockmanager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;


    // Criar categoria
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryDTO dto) {
        System.out.println("🔍 Dados recebidos da categoria: " + dto.getName());
        CategoryDTO createdCategory = categoryService.createCategory(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>>getAllCotegories(){
       List< CategoryDTO> category=categoryService.getAllCategories();
        return ResponseEntity.ok(category);
    }

    // Atualizar categoria
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, dto);
        return ResponseEntity.ok(updatedCategory);
    }

    // Deletar categoria (retornando DTO deletado)
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long id) {
        CategoryDTO deleted = categoryService.deleteCategory(id);
        return ResponseEntity.ok(deleted);
    }

    // Buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/total/{companyId}/total-categories")
    public ResponseEntity<Long> totalOfCategoriesInCompany(@PathVariable Long companyId) {
        try {
            long total = categoryService.getTotalOfCategoriesInCompany(companyId);
            return ResponseEntity.ok(total);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
