package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    // Buscar todos os itens de uma venda
    List<SaleItem> findBySaleId(Long saleId);

    // Total vendido de cada produto (soma quantidade) período
    @Query("SELECT si.product.id AS productId, SUM(si.quantity) AS totalQuantity " +
            "FROM SaleItem si " +
            "JOIN si.sale s " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
            "GROUP BY si.product.id")
    List<Object[]> findTotalQuantityByProductBetweenDates(@Param("startDate") java.time.LocalDateTime startDate,
                                                          @Param("endDate") java.time.LocalDateTime endDate);

    // Total de vendas de um produto
    @Query("SELECT SUM(si.subtotal) FROM SaleItem si WHERE si.product.id = :productId")
    Double findTotalRevenueByProduct(@Param("productId") Long productId);

    // Itens por produto e período (opcional para relatórios detalhados)
    @Query("SELECT si FROM SaleItem si JOIN si.sale s " +
            "WHERE si.product.id = :productId AND s.saleDate BETWEEN :startDate AND :endDate")
    List<SaleItem> findItemsByProductAndPeriod(@Param("productId") Long productId,
                                               @Param("startDate") java.time.LocalDateTime startDate,
                                               @Param("endDate") java.time.LocalDateTime endDate);
}
