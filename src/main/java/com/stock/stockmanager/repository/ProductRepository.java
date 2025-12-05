package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Verifica duplicações (SKU / Barcode / ReferenceNumber)
    boolean existsBySkuOrBarcodeOrReferenceNumber(String sku, String barcode, String referenceNumber);

    // Buscar produtos por empresa
    List<Product> findByCompanyId(Long companyId);

    // Contar produtos da empresa (para dashboard)
    long countByCompanyId(Long companyId);

    // ==== Estatísticas opcionais (o service atual não usa, mas são úteis) ====

    // Contar produtos abaixo do stock mínimo
    @Query("""
            SELECT COUNT(p) 
            FROM Product p 
            WHERE p.company.id = :companyId 
              AND p.quantityInStock < p.minimumStockLevel
            """)
    long countProductsBelowMinStock(Long companyId);

    // Valor total em stock da empresa
    @Query("""
            SELECT SUM(p.sellingPrice * p.quantityInStock) 
            FROM Product p 
            WHERE p.company.id = :companyId
            """)
    Double getTotalMoneyOfProductsInCompany(Long companyId);

    // === Usado pelo Dashboard (gráfico por categoria) ===
    @Query("""
            SELECT p.category.name, COUNT(p) 
            FROM Product p 
            WHERE p.company.id = :companyId 
            GROUP BY p.category.name
            """)
    List<Object[]> countProductsByCategory(Long companyId);
}
