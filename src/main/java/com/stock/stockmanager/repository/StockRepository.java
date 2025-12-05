package com.stock.stockmanager.repository;

import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Stock;
import com.stock.stockmanager.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // =============================
    // MÉTODOS ANTIGOS
    // =============================
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);

    List<Stock> findAllByWarehouse(Warehouse warehouse);

    List<Stock> findAllByProduct(Product product);

    // =============================
    // MÉTODOS DE SOMATÓRIO
    // =============================
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.product.id = :productId")
    Long sumQuantityByProduct(Long productId);

    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM Stock s WHERE s.warehouse.id = :warehouseId")
    Long sumQuantityByWarehouse(Long warehouseId);

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
