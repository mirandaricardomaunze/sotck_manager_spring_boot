package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.Category;
import com.stock.stockmanager.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndCompany(String name, Company company);
    long countByCompanyId(Long companyId);
}
