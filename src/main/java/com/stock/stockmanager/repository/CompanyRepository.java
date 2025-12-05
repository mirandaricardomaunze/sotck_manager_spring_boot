package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository <Company,Long> {
    Optional<Company> findByName(String name);
    List<Company> findByNameContainingIgnoreCase(String name);
    Optional<Company> findByEmail(String email);
    Optional<Company> findByTaxId(String taxId);
    boolean existsByEmail(String email);
    boolean existsByTaxId(String taxId);

}
