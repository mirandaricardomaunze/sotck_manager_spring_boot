package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.enums.PaymentMethod;
import com.stock.stockmanager.enums.SaleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleReportRepository extends JpaRepository<Sale, Long> {

    // Todas as vendas entre datas
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :start AND :end")
    List<Sale> findSalesBetween(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // Todas as vendas de um cliente entre datas
    @Query("SELECT s FROM Sale s WHERE s.clientName LIKE %:clientName% AND s.saleDate BETWEEN :start AND :end")
    List<Sale> findSalesByClientBetween(@Param("clientName") String clientName,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    // Vendas filtradas por status entre datas (opcional)
    @Query("SELECT s FROM Sale s WHERE s.status = :status AND s.saleDate BETWEEN :start AND :end")
    List<Sale> findSalesByStatusBetween(@Param("status") SaleStatus status,
                                        @Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    // Vendas filtradas por método de pagamento entre datas (opcional)
    @Query("SELECT s FROM Sale s WHERE s.paymentMethod = :paymentMethod AND s.saleDate BETWEEN :start AND :end")
    List<Sale> findSalesByPaymentMethodBetween(@Param("paymentMethod") PaymentMethod paymentMethod,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);
}
