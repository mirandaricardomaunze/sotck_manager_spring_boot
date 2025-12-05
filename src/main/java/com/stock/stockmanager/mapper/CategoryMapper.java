package com.stock.stockmanager.mapper;

import com.stock.stockmanager.dto.CategoryDTO;
import com.stock.stockmanager.model.Category;
import com.stock.stockmanager.model.Company;

public class CategoryMapper {

    public static CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getCompany().getId(),
                category.getCompany().getName()
        );
    }

    public static Category toEntity(CategoryDTO dto, Company company) {
        if (dto == null) return null;
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setCompany(company);
        return category;
    }
}
