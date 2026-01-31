package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.MonthlyMovementDTO;
import com.stock.stockmanager.dto.SaleRequestDTO;
import com.stock.stockmanager.dto.SaleResponseDTO;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.model.User;
import com.stock.stockmanager.repository.UserRepository;
import com.stock.stockmanager.service.SaleJasperReportService;
import com.stock.stockmanager.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
@Slf4j
public class SaleController {

    private final SaleService saleService;
    private final SaleJasperReportService saleJasperReportService;
    private final UserRepository userRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaleResponseDTO> registerSale(
            @Valid @RequestBody SaleRequestDTO saleRequest) {

        log.info("Registrando venda | empresaId={} | itens={} | userId={}",
                saleRequest.getCompanyId(), saleRequest.getItems().size(), saleRequest.getUserId());

        // Se quiser garantir que o usuário existe:
        User user = userRepository.findById(saleRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        // Passa para o service
        SaleResponseDTO response = saleService.registerSale(saleRequest, user);
        return ResponseEntity.ok(response);
    }


    // ---------------------------------------------------------------
    // Buscar venda por ID
    // ---------------------------------------------------------------
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaleResponseDTO> getSaleById(@PathVariable Long id) {
        log.info("Buscando venda | id={}", id);
        return ResponseEntity.ok(saleService.findById(id));
    }

    // ---------------------------------------------------------------
    // Cancelar venda
    // ---------------------------------------------------------------
    @PatchMapping(value = "/{id}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id) {
        log.info("Cancelando venda | id={}", id);
        return ResponseEntity.ok(saleService.cancelSale(id));
    }

    // ---------------------------------------------------------------
    // Listar vendas paginadas
    // ---------------------------------------------------------------
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<SaleResponseDTO>> listSales(Pageable pageable) {
        log.info("Listando vendas | page={} | size={}", pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(saleService.listAll(pageable));
    }

    // ---------------------------------------------------------------
    // Deletar venda
    // ---------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        log.info("Deletando venda | id={}", id);
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------------------------------------------------
    // Total de vendas por período
    // ---------------------------------------------------------------
    @GetMapping(value = "/total", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getTotalByPeriod(@RequestParam String period) {
        log.info("Total de vendas | period={}", period);
        return ResponseEntity.ok(saleService.getTotalByPeriod(period));
    }

    // ---------------------------------------------------------------
    // Lucro por período
    // ---------------------------------------------------------------
    @GetMapping(value = "/profit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getProfitByPeriod(@RequestParam String period) {
        log.info("Lucro | period={}", period);
        return ResponseEntity.ok(saleService.getProfitByPeriod(period));
    }

    // ---------------------------------------------------------------
    // Movimento mensal
    // ---------------------------------------------------------------
    @GetMapping(value = "/movement", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MonthlyMovementDTO>> getMonthlyMovement(
            @RequestParam Long companyId,
            @RequestParam String period) {
        log.info("Movimento mensal | companyId={} | period={}", companyId, period);
        return ResponseEntity.ok(saleService.getMonthlyMovement(companyId, period));
    }

    // ---------------------------------------------------------------
    // Exportar relatório em PDF
    // ---------------------------------------------------------------
    @GetMapping(value = "/{id}/report/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> exportSalePdf(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean thermal) {

        log.info("Exportando PDF | id={} | thermal={}", id, thermal);
        byte[] pdfBytes = saleJasperReportService.exportSaleToPdf(id, thermal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sale_" + id + ".pdf\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

    // ---------------------------------------------------------------
    // Exportar relatório em HTML
    // ---------------------------------------------------------------
    @GetMapping(value = "/{id}/report/html", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<byte[]> exportSaleHtml(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean thermal) {

        log.info("Exportando HTML | id={} | thermal={}", id, thermal);
        byte[] htmlBytes = saleJasperReportService.exportSaleToHtml(id, thermal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sale_" + id + ".html\"")
                .contentType(MediaType.TEXT_HTML)
                .contentLength(htmlBytes.length)
                .body(htmlBytes);
    }

    // ---------------------------------------------------------------
    // Exportar relatório em Excel
    // ---------------------------------------------------------------
    @GetMapping(
            value = "/{id}/report/excel",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public ResponseEntity<byte[]> exportSaleExcel(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean thermal) {

        log.info("Exportando Excel | id={} | thermal={}", id, thermal);
        byte[] excelBytes = saleJasperReportService.exportSaleToExcel(id, thermal);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"sale_" + id + ".xlsx\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(excelBytes.length)
                .body(excelBytes);
    }
}
