package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.ProductRequestDTO;
import com.stock.stockmanager.dto.ProductResponseDTO;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.ProductMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository,
                          CompanyRepository companyRepository,
                          WarehouseRepository warehouseRepository,
                          CategoryRepository categoryRepository,
                          SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.companyRepository = companyRepository;
        this.warehouseRepository = warehouseRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    // ========================= CREATE =========================
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Company company = findCompany(dto.getCompanyId());
        Warehouse warehouse = findWarehouse(dto.getWarehouseId());
        Category category = findCategory(dto.getCategoryId());
        Supplier supplier = findSupplier(dto.getSupplierId());

        Product product = ProductMapper.fromRequestDTO(
                dto, company, warehouse, category, supplier
        );

        productRepository.save(product);

        return ProductMapper.toResponseDTO(product);
    }

    // ========================= READ ALL =========================
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ========================= READ BY ID =========================
    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toResponseDTO)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );
    }

    // ========================= UPDATE =========================
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );

        Company company = findCompany(dto.getCompanyId());
        Warehouse warehouse = findWarehouse(dto.getWarehouseId());
        Category category = findCategory(dto.getCategoryId());
        Supplier supplier = findSupplier(dto.getSupplierId());

        ProductMapper.updateEntityFromRequestDTO(
                product, dto, company, warehouse, category, supplier
        );

        return ProductMapper.toResponseDTO(product);
    }

    // ========================= DELETE (soft delete recomendado) =========================
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );

        // Soft delete (boa prática)
        product.setIsActive(false);
    }

    // ========================= STATISTICS (DASHBOARD) =========================

    @Transactional(readOnly = true)
    public long getTotalProductsInCompany(Long companyId) {
        return productRepository.countByCompanyId(companyId);
    }

    @Transactional(readOnly = true)
    public long getProductsBelowMinStock(Long companyId) {
        return productRepository.findByCompanyId(companyId)
                .stream()
                .filter(Product::isBelowMinimum)
                .count();
    }

    @Transactional(readOnly = true)
    public double getTotalValueOfProducts(Long companyId) {
        return productRepository.findByCompanyId(companyId)
                .stream()
                .mapToDouble(p ->
                        p.getSellingPrice().doubleValue() * p.getQuantityInStock()
                )
                .sum();
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getProductsByCategory(Long companyId) {

        List<Object[]> results = productRepository.countProductsByCategory(companyId);
        Map<String, Long> response = new HashMap<>();

        for (Object[] row : results) {
            response.put((String) row[0], (Long) row[1]);
        }

        return response;
    }

    // ========================= MÉTODOS AUXILIARES =========================

    private Company findCompany(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
    }

    private Warehouse findWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
    }

    private Supplier findSupplier(Long id) {
        if (id == null) return null;
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"));
    }
}
