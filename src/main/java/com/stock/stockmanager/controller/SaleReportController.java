package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.SaleReportFilterDTO;
import com.stock.stockmanager.dto.SalesReportDTO;
import com.stock.stockmanager.service.SaleReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports/sales")
@RequiredArgsConstructor
public class SaleReportController {

    private final SaleReportService saleReportService;

    // ---------------------------------------------------------------
    // Relatório padrão (hoje) - GET /api/reports/sales
    // ---------------------------------------------------------------
    @GetMapping
    public ResponseEntity<SalesReportDTO> reportDefault(@ModelAttribute SaleReportFilterDTO filter) {
        // Retorna relatório de hoje por padrão
        return ResponseEntity.ok(saleReportService.reportToday(filter));
    }

    // ---------------------------------------------------------------
    // Relatórios prontos por períodos comuns
    // ---------------------------------------------------------------
    @GetMapping("/today")
    public ResponseEntity<SalesReportDTO> reportToday(@ModelAttribute SaleReportFilterDTO filter) {
        return ResponseEntity.ok(saleReportService.reportToday(filter));
    }

    @GetMapping("/this-month")
    public ResponseEntity<SalesReportDTO> reportThisMonth(@ModelAttribute SaleReportFilterDTO filter) {
        return ResponseEntity.ok(saleReportService.reportThisMonth(filter));
    }

    @GetMapping("/last-3-months")
    public ResponseEntity<SalesReportDTO> reportLast3Months(@ModelAttribute SaleReportFilterDTO filter) {
        return ResponseEntity.ok(saleReportService.reportLast3Months(filter));
    }

    @GetMapping("/last-6-months")
    public ResponseEntity<SalesReportDTO> reportLast6Months(@ModelAttribute SaleReportFilterDTO filter) {
        return ResponseEntity.ok(saleReportService.reportLast6Months(filter));
    }

    @GetMapping("/last-12-months")
    public ResponseEntity<SalesReportDTO> reportLast12Months(@ModelAttribute SaleReportFilterDTO filter) {
        return ResponseEntity.ok(saleReportService.reportLast12Months(filter));
    }

    // ---------------------------------------------------------------
    // Relatório customizado por período
    // ---------------------------------------------------------------
    @GetMapping("/custom")
    public ResponseEntity<SalesReportDTO> getCustomReport(
            @RequestParam("startDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam("endDate") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @ModelAttribute SaleReportFilterDTO filter
    ) {
        java.time.LocalDateTime start = startDate.atStartOfDay();
        java.time.LocalDateTime end = endDate.atTime(23, 59, 59);

        SalesReportDTO report = saleReportService.getReport(start, end, filter,
                startDate + " to " + endDate);

        return ResponseEntity.ok(report);
    }
}
