package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.dto.SalesReportDTO;
import com.stock.stockmanager.dto.SaleReportFilterDTO;
import com.stock.stockmanager.mapper.SaleMapper;
import com.stock.stockmanager.model.Sale;
import com.stock.stockmanager.repository.SaleReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleReportService {

    private final SaleReportRepository saleRepository;
    private final SaleMapper saleMapper;

    // ---------------------------------------------------------------
    // Relatório genérico por período com filtros
    // ---------------------------------------------------------------
    public SalesReportDTO getReport(LocalDateTime start, LocalDateTime end,
                                    SaleReportFilterDTO filter, String periodName) {

        List<Sale> sales;

        // Filtro por cliente
        if (filter != null && filter.getClientName() != null && !filter.getClientName().isBlank()) {
            sales = saleRepository.findSalesByClientBetween(filter.getClientName(), start, end);
        } else {
            sales = saleRepository.findSalesBetween(start, end);
        }

        // Aplicar filtro por método de pagamento e status
        if (filter != null) {
            if (filter.getPaymentMethod() != null) {
                sales = sales.stream()
                        .filter(s -> s.getPaymentMethod() == filter.getPaymentMethod())
                        .collect(Collectors.toList());
            }
            if (filter.getStatus() != null) {
                sales = sales.stream()
                        .filter(s -> s.getStatus() == filter.getStatus())
                        .collect(Collectors.toList());
            }
        }

        // Calcular receita total
        BigDecimal total = sales.stream()
                .map(s -> s.getTotalAmount() != null ? s.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Mapear para DTO
        List<SaleResponseDTO> saleDTOs = sales.stream()
                .map(saleMapper::toDTO)
                .collect(Collectors.toList());

        // Retornar relatório
        return SalesReportDTO.builder()
                .period(periodName)
                .totalRevenue(total)
                .totalSales((long) sales.size())
                .sales(saleDTOs)
                .build();
    }

    // ---------------------------------------------------------------
    // Métodos prontos para períodos comuns
    // ---------------------------------------------------------------
    public SalesReportDTO reportToday(SaleReportFilterDTO filter) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return getReport(start, end, filter, "Hoje");
    }

    public SalesReportDTO reportThisMonth(SaleReportFilterDTO filter) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);
        return getReport(start, end, filter, "Este Mês");
    }

    public SalesReportDTO reportLast3Months(SaleReportFilterDTO filter) {
        LocalDateTime start = LocalDate.now().minusMonths(3).withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return getReport(start, end, filter, "Últimos 3 Meses");
    }

    public SalesReportDTO reportLast6Months(SaleReportFilterDTO filter) {
        LocalDateTime start = LocalDate.now().minusMonths(6).withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return getReport(start, end, filter, "Últimos 6 Meses");
    }

    public SalesReportDTO reportLast12Months(SaleReportFilterDTO filter) {
        LocalDateTime start = LocalDate.now().minusMonths(12).withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return getReport(start, end, filter, "Últimos 12 Meses");
    }
}
