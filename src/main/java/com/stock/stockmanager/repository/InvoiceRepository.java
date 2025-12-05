package com.stock.stockmanager.repository;

import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrder_OrderNumber(String orderNumber);
    List<Invoice> findByOrder_OrderNumber(String orderNumber);
    List<Invoice> findByStatus(InvoiceStatus status);
    Optional<Invoice> findFirstByOrder_OrderNumber(String orderNumber);
}
