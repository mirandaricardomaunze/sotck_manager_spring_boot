package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.SupplierDTO;
import com.stock.stockmanager.exception.DuplicateResourceException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.SupplierMapper;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Supplier;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final CompanyRepository companyRepository;

    public SupplierService(SupplierRepository supplierRepository, CompanyRepository companyRepository) {
        this.supplierRepository = supplierRepository;
        this.companyRepository = companyRepository;
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll()
                .stream()
                .map(SupplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        if (supplierDTO.getName() == null || supplierDTO.getName().trim().isEmpty()) {
            throw new DuplicateResourceException("Nome do fornecedor é obrigatório");
        }
        if (supplierDTO.getCompanyId() == null) {
            throw new ResourceNotFoundException("CompanyId é obrigatório");
        }

        Company company = companyRepository.findById(supplierDTO.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        if (supplierRepository.existsByNuitAndCompany(supplierDTO.getNuit(), company)) {
            throw new DuplicateResourceException("NUIT já cadastrado para essa empresa");
        }

        if (supplierRepository.existsByNameAndCompany(supplierDTO.getName(), company)) {
            throw new DuplicateResourceException("Nome já cadastrado para essa empresa");
        }

        Supplier supplierEntity = SupplierMapper.toEntity(supplierDTO, company);
        Supplier saved = supplierRepository.save(supplierEntity);
        return SupplierMapper.toDTO(saved);
    }

    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com id " + id));

        Company company = companyRepository.findById(supplierDTO.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada com id " + supplierDTO.getCompanyId()));

        if (supplierDTO.getNuit() != null &&
                supplierRepository.existsByNuitAndCompanyAndIdNot(supplierDTO.getNuit().trim(), company, id)) {
            throw new DuplicateResourceException("NUIT já cadastrado para essa empresa");
        }

        if (!Objects.equals(supplier.getName(), supplierDTO.getName()) &&
                supplierDTO.getName() != null &&
                supplierRepository.existsByNameAndCompanyAndIdNot(supplierDTO.getName().trim(), company, id)) {
            throw new DuplicateResourceException("Nome do fornecedor já cadastrado para essa empresa");
        }
        supplier.setName(supplierDTO.getName());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setPhone(supplierDTO.getPhone());
        supplier.setNuit(supplierDTO.getNuit());
        supplier.setAddress(supplierDTO.getAddress());
        supplier.setWebsite(supplierDTO.getWebsite());
        supplier.setNotes(supplierDTO.getNotes());
        supplier.setCompany(company);

        Supplier saved = supplierRepository.save(supplier);
        return SupplierMapper.toDTO(saved);
    }

    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Fornecedor não encontrado com id " + id));
        return SupplierMapper.toDTO(supplier);
    }

    public SupplierDTO deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor não encontrado com id " + id));
        supplierRepository.delete(supplier);
        return SupplierMapper.toDTO(supplier);
    }

    public List<SupplierDTO> searchSuppliers(String name, String nuit, Long companyId) {
        String searchName = (name!=null? name:"");
        String searchNuit = (nuit!=null? nuit:"");

         List<Supplier> suppliers;

        if (companyId != null) {
           suppliers= supplierRepository
                   .findByNameAndNuitAndCompanyId(searchName, searchNuit, companyId);
        }else{
            suppliers=supplierRepository.findAll().stream()
                    .filter(supplier ->  supplier.getName().toLowerCase().contains(searchName.toLowerCase()))
                    .filter(supplier -> searchNuit.isEmpty() || (supplier.getNuit() != null && supplier.getNuit().toLowerCase().contains(searchNuit.toLowerCase())))
                    .collect(Collectors.toList());
        }
        return  suppliers.stream()
                .map(SupplierMapper::toDTO)
                .collect(Collectors.toList());
    }



}
