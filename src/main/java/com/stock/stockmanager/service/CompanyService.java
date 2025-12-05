package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.CompanyDTO;
import com.stock.stockmanager.exception.DuplicateResourceException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.repository.CompanyRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // ===== CREATE =====
    public CompanyDTO createCompany(CompanyDTO dto) {
        if (companyRepository.existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Email já cadastrado");
        }
        if (companyRepository.existsByTaxId(dto.getTaxId())){
            throw new DuplicateResourceException("Nuit já cadastrado");
        }

        Company company = fromDTO(dto);
        company = companyRepository.save(company);
        return toDTO(company);
    }

    // ===== READ ALL =====
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== READ BY ID =====
    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        return toDTO(company);
    }

    // ===== SEARCH BY NAME =====
    public List<CompanyDTO> searchCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ===== UPDATE =====
    public CompanyDTO updateCompany(Long id, CompanyDTO dto) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        existing.setName(dto.getName());
        existing.setAddress(dto.getAddress());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setEmail(dto.getEmail());
        existing.setWebsite(dto.getWebsite());
        existing.setTaxId(dto.getTaxId());
        existing.setRegistrationNumber(dto.getRegistrationNumber());
        existing.setLogoUrl(dto.getLogoUrl());
        existing.setDescription(dto.getDescription());
        existing.setCountry(dto.getCountry());
        existing.setCity(dto.getCity());
        existing.setPostalCode(dto.getPostalCode());
        existing.setIndustry(dto.getIndustry());
        existing.setContactEmail(dto.getContactEmail());
        existing.setContactPhone(dto.getContactPhone());

        return toDTO(companyRepository.save(existing));
    }

    // ===== DELETE =====
    public void deleteCompany(Long id) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        companyRepository.delete(existing);
    }

    // ===== Mappers usando setters =====
    private CompanyDTO toDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setPhoneNumber(company.getPhoneNumber());
        dto.setEmail(company.getEmail());
        dto.setWebsite(company.getWebsite());
        dto.setTaxId(company.getTaxId());
        dto.setRegistrationNumber(company.getRegistrationNumber());
        dto.setLogoUrl(company.getLogoUrl());
        dto.setDescription(company.getDescription());
        dto.setCountry(company.getCountry());
        dto.setCity(company.getCity());
        dto.setPostalCode(company.getPostalCode());
        dto.setIndustry(company.getIndustry());
        dto.setContactEmail(company.getContactEmail());
        dto.setContactPhone(company.getContactPhone());
        return dto;
    }

    private Company fromDTO(CompanyDTO dto) {
        Company company = new Company();
        company.setName(dto.getName());
        company.setAddress(dto.getAddress());
        company.setPhoneNumber(dto.getPhoneNumber());
        company.setEmail(dto.getEmail());
        company.setWebsite(dto.getWebsite());
        company.setTaxId(dto.getTaxId());
        company.setRegistrationNumber(dto.getRegistrationNumber());
        company.setLogoUrl(dto.getLogoUrl());
        company.setDescription(dto.getDescription());
        company.setCountry(dto.getCountry());
        company.setCity(dto.getCity());
        company.setPostalCode(dto.getPostalCode());
        company.setIndustry(dto.getIndustry());
        company.setContactEmail(dto.getContactEmail());
        company.setContactPhone(dto.getContactPhone());
        return company;
    }
}
