package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.OrderSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSequenceRepository extends JpaRepository<OrderSequence, Long> {
}
