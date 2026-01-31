package com.stock.stockmanager.repository;

import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Stock;
import com.stock.stockmanager.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // =============================
    // MÉTODOS DE BUSCA
    // =============================

    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);

    List<Stock> findAllByWarehouse(Warehouse warehouse);

    List<Stock> findAllByProduct(Product product);

    Optional<Stock> findByProduct_IdAndWarehouse_Id(Long productId, Long warehouseId);

    // Correção: navegação via relacionamento (Warehouse -> Company)
    Optional<Stock> findByWarehouse_CompanyAndWarehouseAndProduct(
            Company company,
            Warehouse warehouse,
            Product product
    );

    // Ou usando @Query explícita
    @Query("""
        SELECT s
        FROM Stock s
        WHERE s.warehouse.company = :company
          AND s.warehouse = :warehouse
          AND s.product = :product
    """)
    Optional<Stock> findByCompanyAndWarehouseAndProduct(
            @Param("company") Company company,
            @Param("warehouse") Warehouse warehouse,
            @Param("product") Product product
    );

    // =============================
    // MÉTODOS DE SOMATÓRIO
    // =============================

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.product.id = :productId")
    Long sumQuantityByProduct(@Param("productId") Long productId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.warehouse.id = :warehouseId")
    Long sumQuantityByWarehouse(@Param("warehouseId") Long warehouseId);

    // =============================
    // SUMARIZAÇÕES PARA DASHBOARD
    // =============================

    @Query("""
           SELECT new com.stock.stockmanager.dto.StockSummaryDTO(
               s.product.name,
               SUM(s.quantity)
           )
           FROM Stock s
           GROUP BY s.product.name
           """)
    List<StockSummaryDTO> getStockSummaryByProduct();

    @Query("""
           SELECT new com.stock.stockmanager.dto.StockSummaryDTO(
               s.warehouse.name,
               SUM(s.quantity)
           )
           FROM Stock s
           GROUP BY s.warehouse.name
           """)
    List<StockSummaryDTO> getStockSummaryByWarehouse();
}
