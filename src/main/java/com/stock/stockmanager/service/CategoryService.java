package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.CategoryDTO;
import com.stock.stockmanager.exception.DuplicateResourceException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.CategoryMapper;
import com.stock.stockmanager.model.Category;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.repository.CategoryRepository;
import com.stock.stockmanager.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           CompanyRepository companyRepository) {
        this.categoryRepository = categoryRepository;
        this.companyRepository = companyRepository;
    }

    // Criar categoria
    public CategoryDTO createCategory(CategoryDTO dto) {
        // Busca empresa
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        if (categoryRepository.existsByNameAndCompany(dto.getName(), company)) {
            throw new DuplicateResourceException("Categoria já cadastrada para essa empresa");
        }
        // Converte DTO → Entity
        Category category = CategoryMapper.toEntity(dto, company);
        category = categoryRepository.save(category);
        return CategoryMapper.toDTO(category);
    }


    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        if (!category.getName().equals(dto.getName()) &&
                categoryRepository.existsByNameAndCompany(dto.getName(), company)) {
            throw new DuplicateResourceException("O nome da categoria já está cadastrado para essa empresa");
        }

        category.setName(dto.getName());
        category.setCompany(company);
        Category updatedCategory = categoryRepository.save(category);
        return CategoryMapper.toDTO(updatedCategory);
    }



    // Listar todas as categorias
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Buscar categoria por id
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return CategoryMapper.toDTO(category);
    }

    // Deletar categoria
    public CategoryDTO  deleteCategory(Long id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
      CategoryDTO deletedCategory = CategoryMapper.toDTO(existing);
      categoryRepository.delete(existing);
      return deletedCategory;
    }

    public long getTotalOfCategoriesInCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        long total = categoryRepository.countByCompanyId(companyId);
        System.out.println("Total de categorias na empresa " + company.getName() + ": " + total);
        return total;
    }
}


