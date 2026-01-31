package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.MonthlyMovementDTO;
import com.stock.stockmanager.dto.SaleRequestDTO;
import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.enums.SaleStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.SaleMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final CompanyRepository companyRepository;
    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final SaleMapper saleMapper;

    // ---------------------------------------------------------------
    // REGISTRAR VENDA
    // ---------------------------------------------------------------
    @Transactional
    public SaleResponseDTO registerSale(SaleRequestDTO dto, User user) {

        if (!dto.isValid()) throw new BusinessException("Dados inválidos para venda.");

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado."));

        if (!warehouse.getCompany().getId().equals(company.getId()))
            throw new BusinessException("Armazém não pertence à empresa.");

        // ✅ Inclui o usuário ao criar a venda
        Sale sale = saleMapper.toEntity(dto, company, user);
        sale.setWarehouse(warehouse);
        sale.setStatus(SaleStatus.COMPLETED);
        sale.setSaleDate(LocalDateTime.now());

        Sale savedSale = saleRepository.save(sale);
        BigDecimal total = BigDecimal.ZERO;

        for (var itemDTO : dto.getItems()) {
            if (!itemDTO.isValid()) throw new BusinessException("Item inválido na venda.");

            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado."));
            Stock stock = stockRepository.findByCompanyAndWarehouseAndProduct(company, warehouse, product)
                    .orElseThrow(() -> new BusinessException("Produto " + product.getName() + " não existe no armazém."));
            if (stock.getQuantity() < itemDTO.getQuantity())
                throw new BusinessException("Stock insuficiente para o produto " + product.getName());

            // baixa stock
            stock.setQuantity(stock.getQuantity() - itemDTO.getQuantity());
            stockRepository.save(stock);

            // Cálculo subtotal e imposto
            BigDecimal unitPrice = itemDTO.getUnitPrice() != null ? itemDTO.getUnitPrice() : product.getSellingPrice();
            BigDecimal taxPercentage = product.getTaxPercentage() != null ? product.getTaxPercentage() : BigDecimal.ZERO;
            boolean taxIncluded = Boolean.TRUE.equals(product.getIsTaxIncluded());

            BigDecimal taxAmount;
            BigDecimal subtotal;

            if (taxIncluded) {
                subtotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity())).setScale(2, RoundingMode.HALF_UP);
                taxAmount = subtotal.multiply(taxPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                taxAmount = unitPrice.multiply(taxPercentage).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
                subtotal = unitPrice.multiply(BigDecimal.valueOf(itemDTO.getQuantity()))
                        .add(taxAmount).setScale(2, RoundingMode.HALF_UP);
            }

            SaleItem item = saleMapper.toItemEntity(itemDTO, savedSale, product);
            item.setSubtotal(subtotal);
            item.setTaxAmount(taxAmount);

            saleItemRepository.save(item);
            total = total.add(subtotal);
        }

        savedSale.setTotalAmount(total);
        savedSale.setChange(savedSale.getAmountPaid().subtract(total.subtract(savedSale.getDiscount())));
        saleRepository.save(savedSale);

        log.info("Venda registrada com sucesso. ID {}", savedSale.getId());
        return saleMapper.toDTO(savedSale);
    }


    // ---------------------------------------------------------------
    // CANCELAR VENDA
    // ---------------------------------------------------------------
    @Transactional
    public SaleResponseDTO cancelSale(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
        if (sale.getStatus() == SaleStatus.CANCELED)
            throw new BusinessException("Venda já está cancelada.");

        for (SaleItem item : sale.getItems()) {
            Stock stock = stockRepository.findByCompanyAndWarehouseAndProduct(sale.getCompany(), sale.getWarehouse(), item.getProduct())
                    .orElseThrow(() -> new BusinessException("Stock não encontrado para devolução."));
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockRepository.save(stock);
        }

        sale.setStatus(SaleStatus.CANCELED);
        saleRepository.save(sale);

        log.info("Venda ID {} cancelada.", saleId);
        return saleMapper.toDTO(sale);
    }

    // ---------------------------------------------------------------
    // BUSCAR POR ID
    // ---------------------------------------------------------------
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
        return saleMapper.toDTO(sale);
    }

    // ---------------------------------------------------------------
    // DELETAR VENDA
    // ---------------------------------------------------------------
    public void delete(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venda não encontrada."));
        saleRepository.delete(sale);
        log.warn("Venda ID {} deletada.", id);
    }

    // ---------------------------------------------------------------
    // LISTAR PAGINADO
    // ---------------------------------------------------------------
    public Page<SaleResponseDTO> listAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(saleMapper::toDTO);
    }

    // ---------------------------------------------------------------
    // TOTAL DE VENDAS POR PERÍODO
    // ---------------------------------------------------------------
    public BigDecimal getTotalByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = resolvePeriod(period, now);
        return saleRepository.getTotalByPeriod(start, now);
    }

    // ---------------------------------------------------------------
    // LUCRO POR PERÍODO
    // ---------------------------------------------------------------
    public BigDecimal getProfitByPeriod(String period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = resolvePeriod(period, now);
        return saleRepository.getProfitBetween(start, now);
    }

    private LocalDateTime resolvePeriod(String period, LocalDateTime now) {
        return switch (period.toLowerCase()) {
            case "today" -> now.toLocalDate().atStartOfDay();
            case "month" -> now.withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "3months" -> now.minusMonths(3).withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "6months" -> now.minusMonths(6).withDayOfMonth(1).toLocalDate().atStartOfDay();
            case "1year" -> now.minusYears(1).withDayOfYear(1).toLocalDate().atStartOfDay();
            default -> throw new IllegalArgumentException("Período inválido.");
        };
    }

    // ---------------------------------------------------------------
    // MOVIMENTO MENSAL
    // ---------------------------------------------------------------
    public List<MonthlyMovementDTO> getMonthlyMovement(Long companyId, String period) {

        LocalDateTime now = LocalDate.now().atStartOfDay();
        LocalDateTime startDate = switch (period) {
            case "3months" -> now.minusMonths(3).withDayOfMonth(1);
            case "6months" -> now.minusMonths(6).withDayOfMonth(1);
            case "1year" -> now.minusYears(1).withDayOfMonth(1);
            default -> now.minusMonths(1).withDayOfMonth(1);
        };

        List<Object[]> raw = saleRepository.getMonthlySalesByCompanyAndPeriod(companyId, startDate);

        return raw.stream()
                .map(r -> {
                    Integer month = ((Number) r[0]).intValue();
                    Long total = ((Number) r[1]).longValue();
                    String monthName = Month.of(month).getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
                    return new MonthlyMovementDTO(monthName, total);
                })
                .collect(Collectors.toList());
    }
}
