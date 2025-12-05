package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    Optional<Warehouse> findByName(String name);

    Optional<Warehouse> findByLocation(String location);

    Optional<Warehouse> findByEmail(String email);

    boolean existsByName(String name);

    boolean existsByLocation(String location);

    boolean existsByEmail(String email);

    Optional<Warehouse> findById(Long id);

    // NOVO MÉTODO: retorna todos os armazéns principais de uma empresa
    List<Warehouse> findByCompanyIdAndPrincipalTrue(Long companyId);
    List<Warehouse> findByCompanyId(Long companyId);

}
