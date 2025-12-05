package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCompany_Id(Long companyId);
    List<Order> findByWarehouse_Id(Long warehouseId);
    Optional<Order> findByOrderNumber(String orderNumber);
}
