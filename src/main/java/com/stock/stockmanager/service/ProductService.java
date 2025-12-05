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
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Supplier supplier = dto.getSupplierId() != null
                ? supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"))
                : null;

        Product product = ProductMapper.fromRequestDTO(dto, company, warehouse, category, supplier);
        productRepository.save(product);

        return ProductMapper.toResponseDTO(product);
    }

    // ========================= READ ALL =========================
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ========================= READ BY ID =========================
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );

        return ProductMapper.toResponseDTO(product);
    }

    // ========================= UPDATE =========================
    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        Supplier supplier = dto.getSupplierId() != null
                ? supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado"))
                : null;

        ProductMapper.updateEntityFromRequestDTO(product, dto, company, warehouse, category, supplier);

        productRepository.save(product);

        return ProductMapper.toResponseDTO(product);
    }

    // ========================= DELETE =========================
    @Transactional
    public void deleteProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Produto não encontrado com id " + id)
                );

        productRepository.delete(product);
    }

    // ========================= STATISTICS (DASHBOARD) =========================

    /** Total de produtos da empresa */
    public long getTotalProductsInCompany(Long companyId) {
        return productRepository.countByCompanyId(companyId);
    }

    /** Produtos abaixo do stock mínimo */
    public long getProductsBelowMinStock(Long companyId) {
        return productRepository.findByCompanyId(companyId)
                .stream()
                .filter(Product::isBelowMinimum)
                .count();
    }

    /** Valor total de todos os produtos da empresa */
    public double getTotalValueOfProducts(Long companyId) {
        return productRepository.findByCompanyId(companyId)
                .stream()
                .mapToDouble(p -> p.getSellingPrice().doubleValue() * p.getQuantityInStock())
                .sum();
    }

    /** Quantidade de produtos por categoria (para gráfico Pie) */
    public Map<String, Long> getProductsByCategory(Long companyId) {

        List<Object[]> results = productRepository.countProductsByCategory(companyId);
        Map<String, Long> map = new HashMap<>();

        for (Object[] row : results) {
            String categoryName = (String) row[0];
            Long count = (Long) row[1];
            map.put(categoryName, count);
        }

        return map;
    }
}
