package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.enums.SaleStatus;
import com.stock.stockmanager.enums.PaymentMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    /* ------------------------------
        TOTAL DE VENDAS ENTRE DATAS
    ------------------------------ */
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s " +
            "WHERE s.saleDate BETWEEN :start AND :end")
    BigDecimal getTotalSalesBetween(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);

    /* ------------------------------
        LISTA DE VENDAS ENTRE DATAS
    ------------------------------ */
    @Query("SELECT s FROM Sale s " +
            "WHERE s.saleDate BETWEEN :start AND :end " +
            "ORDER BY s.saleDate DESC")
    List<Sale> findSalesBetween(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    /* ------------------------------
        TOTAL POR MÉTODO DE PAGAMENTO
    ------------------------------ */
    @Query("SELECT COALESCE(SUM(s.totalAmount),0) FROM Sale s " +
            "WHERE s.saleDate BETWEEN :start AND :end " +
            "AND s.paymentMethod = :paymentMethod")
    BigDecimal getTotalSalesByPayment(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end,
                                      @Param("paymentMethod") PaymentMethod paymentMethod);

    /* ------------------------------
        TOTAL POR STATUS
    ------------------------------ */
    @Query("SELECT COALESCE(SUM(s.totalAmount),0) FROM Sale s " +
            "WHERE s.saleDate BETWEEN :start AND :end " +
            "AND s.status = :status")
    BigDecimal getTotalSalesByStatus(@Param("start") LocalDateTime start,
                                     @Param("end") LocalDateTime end,
                                     @Param("status") SaleStatus status);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.saleDate >= :start AND s.saleDate <= :end")
    BigDecimal getTotalByPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /* ------------------------------
        BUSCA VENDAS POR CLIENTE ENTRE DATAS
    ------------------------------ */
    @Query("SELECT s FROM Sale s " +
            "WHERE s.saleDate BETWEEN :start AND :end " +
            "AND LOWER(s.clientName) LIKE LOWER(CONCAT('%',:clientName,'%')) " +
            "ORDER BY s.saleDate DESC")
    List<Sale> findSalesByClientBetween(@Param("clientName") String clientName,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM((si.unitPrice - p.costPrice) * si.quantity), 0)
    FROM SaleItem si
    JOIN si.sale s
    JOIN si.product p
    WHERE s.saleDate BETWEEN :start AND :end
""")
    BigDecimal getProfitBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query("""
    SELECT EXTRACT(MONTH FROM s.saleDate) AS month, SUM(si.quantity) AS total
    FROM SaleItem si
    JOIN si.sale s
    WHERE s.company.id = :companyId
      AND s.saleDate >= :startDate
    GROUP BY EXTRACT(MONTH FROM s.saleDate)
    ORDER BY month
""")
    List<Object[]> getMonthlySalesByCompanyAndPeriod(
            @Param("companyId") Long companyId,
            @Param("startDate") LocalDateTime startDate
    );



}
