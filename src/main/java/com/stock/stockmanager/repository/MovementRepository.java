package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Movement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByDateBetween(LocalDateTime start, LocalDateTime end);
    List<Movement> findByCompany(Company company);
    List<Movement> findByCompanyAndDateBetween(Company company, LocalDateTime start, LocalDateTime end);


}

