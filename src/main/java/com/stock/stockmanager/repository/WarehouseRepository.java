package com.stock.stockmanager.repository;

import com.stock.stockmanager.enums.WarehouseStatus;
import com.stock.stockmanager.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    List<Warehouse> findByCompanyId(Long companyId);

    List<Warehouse> findByCompanyIdAndStatus(Long companyId, WarehouseStatus status);

    List<Warehouse> findByCompanyIdAndPrincipalTrue(Long companyId);

    Optional<Warehouse> findByEmail(String email);

    Optional<Warehouse> findById(Long id);


}
