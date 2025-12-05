package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.CategoryDTO;
import com.stock.stockmanager.dto.SupplierDTO;
import com.stock.stockmanager.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<SupplierDTO>createSupplier(@RequestBody SupplierDTO supplierDTO ){
        SupplierDTO createdSupplier=supplierService.createSupplier(supplierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO>updateSupplier( @PathVariable Long id,  @RequestBody SupplierDTO supplierDTO) {
        SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
        return ResponseEntity.ok(updatedSupplier);
    }

    @GetMapping
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        List<SupplierDTO>suppliers=supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO>getSupplierById(@PathVariable Long id){
        SupplierDTO supplier=supplierService.getSupplierById(id);
        return ResponseEntity.ok(supplier);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SupplierDTO> deleteSupplier(@PathVariable Long id) {
        SupplierDTO supplier = supplierService.deleteSupplier(id);
        return ResponseEntity.ok(supplier);
    }

   @GetMapping("/search")
    public ResponseEntity<List<SupplierDTO>>searchSuppliers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String nuit,
            @RequestParam(required = false) Long companyId
   ){
        List<SupplierDTO> result=supplierService.searchSuppliers(name,nuit,companyId);
        return ResponseEntity.ok(result);

   }


}
