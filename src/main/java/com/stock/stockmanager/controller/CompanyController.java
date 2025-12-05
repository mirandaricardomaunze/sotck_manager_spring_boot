package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.CompanyDTO;
import com.stock.stockmanager.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
@RequiredArgsConstructor
public class CompanyController {
    @Autowired
    private final CompanyService companyService;

    // ===== CREATE =====
    @PostMapping
    public ResponseEntity<CompanyDTO> create(@RequestBody CompanyDTO dto) {
        CompanyDTO created = companyService.createCompany(dto);
        return ResponseEntity.ok(created);
    }

    // ===== READ ALL =====
    @GetMapping
    public ResponseEntity<List<CompanyDTO>> getAll() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // ===== READ BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    // ===== SEARCH BY NAME =====
    @GetMapping("/search")
    public ResponseEntity<List<CompanyDTO>> searchByName(@RequestParam String name) {
        List<CompanyDTO> companies = companyService.searchCompaniesByName(name);
        return ResponseEntity.ok(companies);
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<CompanyDTO> update(@PathVariable Long id, @RequestBody CompanyDTO dto) {
        CompanyDTO updated = companyService.updateCompany(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
