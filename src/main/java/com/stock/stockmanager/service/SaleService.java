package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.MonthlyMovementDTO;
import com.stock.stockmanager.dto.SaleRequestDTO;
import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.SaleMapper;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.model.SaleItem;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.ProductRepository;
import com.stock.stockmanager.repository.SaleItemRepository;
import com.stock.stockmanager.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final SaleMapper saleMapper;

    // ---------------------------------------------------------------
    // Registrar nova venda
    // ---------------------------------------------------------------
    public SaleResponseDTO registerSale(SaleRequestDTO dto) {
        log.info("Registrando venda para empresa {}", dto.getCompanyId());

        if (!dto.isValid()) throw new BusinessException("Dados inválidos para venda.");

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        Sale sale = saleMapper.toEntity(dto, company);
        sale.setSaleDate(LocalDateTime.now());

        Sale savedSale = saleRepository.save(sale);
        BigDecimal total = BigDecimal.ZERO;

        for (var itemDTO : dto.getItems()) {
            if (!itemDTO.isValid()) throw new BusinessException("Item inválido na venda.");

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto ID " + itemDTO.getProductId() + " não encontrado."));

            if (product.getQuantityInStock() < itemDTO.getQuantity()) {
                throw new BusinessException("Stock insuficiente para produto " + product.getName());
            }

            SaleItem item = saleMapper.toItemEntity(itemDTO, savedSale, product);
            item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            saleItemRepository.save(item);

            total = total.add(item.getSubtotal());

            product.setQuantityInStock(product.getQuantityInStock() - item.getQuantity());
            productRepository.save(product);
        }

        savedSale.setTotalAmount(total);
        savedSale.setChange(savedSale.getAmountPaid()
                .subtract(total.subtract(savedSale.getDiscount())));
        saleRepository.save(savedSale);

        log.info("Venda registrada com sucesso. ID: {}", savedSale.getId());
        return saleMapper.toDTO(savedSale);
    }

    // ---------------------------------------------------------------
    // Buscar venda por ID
    // ---------------------------------------------------------------
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
        return saleMapper.toDTO(sale);
    }

    // ---------------------------------------------------------------
    // Listar vendas paginadas
    // ---------------------------------------------------------------
    public Page<SaleResponseDTO> listAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(saleMapper::toDTO);
    }

    // ---------------------------------------------------------------
    // Deletar venda
    // ---------------------------------------------------------------
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
        saleRepository.delete(sale);
        log.warn("Venda ID {} deletada.", id);
    }

    public BigDecimal getTotalByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period.toLowerCase()) {
            case "today":
                start = now.toLocalDate().atStartOfDay();
                break;
            case "month":
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "3months":
                start = now.minusMonths(3).withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "6months":
                start = now.minusMonths(6).withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "1year":
                start = now.minusYears(1).withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Período inválido: " + period);
        }

        return saleRepository.getTotalByPeriod(start, now);
    }

    public BigDecimal getProfitByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        switch (period.toLowerCase()) {
            case "today":
                start = now.toLocalDate().atStartOfDay();
                break;
            case "month":
                start = now.withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "3months":
                start = now.minusMonths(3).withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "6months":
                start = now.minusMonths(6).withDayOfMonth(1).toLocalDate().atStartOfDay();
                break;
            case "1year":
                start = now.minusYears(1).withDayOfYear(1).toLocalDate().atStartOfDay();
                break;
            default:
                throw new IllegalArgumentException("Período inválido: " + period);
        }

        return saleRepository.getProfitBetween(start, now);
    }


    public List<MonthlyMovementDTO> getMonthlyMovement(Long companyId, String period) {
        // Data atual
        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime startDate;

        switch (period) {
            case "3months":
                startDate = now.minusMonths(3).withDayOfMonth(1);
                break;
            case "6months":
                startDate = now.minusMonths(6).withDayOfMonth(1);
                break;
            case "1year":
                startDate = now.minusYears(1).withDayOfMonth(1);
                break;
            case "month":
            default:
                startDate = now.minusMonths(1).withDayOfMonth(1);
                break;
        }

        // Busca vendas no repositório filtrando pela empresa e período
        List<Object[]> raw = saleRepository.getMonthlySalesByCompanyAndPeriod(companyId, startDate);

        return raw.stream()
                .map(r -> {
                    Integer monthNumber = ((Number) r[0]).intValue();
                    Long totalQuantity = ((Number) r[1]).longValue();
                    String monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
                    return new MonthlyMovementDTO(monthName, totalQuantity);
                })
                .collect(Collectors.toList());
    }

}
