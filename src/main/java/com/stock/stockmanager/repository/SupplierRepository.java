package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByNuitAndCompanyAndIdNot(String nuit, Company company, Long id);
    boolean existsByNameAndCompanyAndIdNot(String name, Company company, Long id);
    boolean existsByNameAndCompany(String name, Company company);
    boolean existsByNuitAndCompany(String nuit, Company company);
    List<Supplier> findByNameAndNuitAndCompanyId(
            String name, String nuit, Long companyId
    );
}
