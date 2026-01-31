package com.stock.stockmanager.controller;

import com.stock.stockmanager.dto.InvoiceDTO;
import com.stock.stockmanager.service.InvoiceReportService;
import com.stock.stockmanager.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoiceReportService invoiceReportService;

    public InvoiceController(InvoiceService invoiceService, InvoiceReportService invoiceReportService) {
        this.invoiceService = invoiceService;
        this.invoiceReportService = invoiceReportService;
    }

    // ========================
    // CRIAÇÃO DE FATURA
    // ========================
    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestParam String orderNumber) {
        InvoiceDTO invoice = invoiceService.createInvoiceFromOrderNumber(orderNumber);
        return ResponseEntity.status(201).body(invoice);
    }

    // ========================
    // LISTAGEM DE FATURAS
    // ========================
    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return invoices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(invoices);
    }

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByOrderNumber(@PathVariable String orderNumber) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByOrderNumber(orderNumber);
        return invoices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(invoices);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByStatus(@PathVariable String status) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status);
        return invoices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(invoices);
    }

    // ========================
    // BUSCAR POR ID
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========================
    // CANCELAMENTO DE FATURA
    // ========================
    @PutMapping("/{id}/cancel")
    public ResponseEntity<InvoiceDTO> cancelInvoice(@PathVariable Long id) {
        InvoiceDTO canceled = invoiceService.cancelInvoice(id);
        return ResponseEntity.ok(canceled);
    }

    // ========================
    // ATUALIZAÇÃO DE STATUS
    // ========================
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(@PathVariable Long id, @PathVariable String status) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(updatedInvoice);
    }

    // ========================
    // EXCLUSÃO DE FATURA
    // ========================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    // ========================
    // EXPORTAÇÃO E PREVIEW
    // ========================
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable Long id) {
        byte[] data = invoiceReportService.exportInvoiceToPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=fatura_" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(data);
    }

    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportInvoiceExcel(@PathVariable Long id) {
        byte[] data = invoiceReportService.exportInvoiceToExcel(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=fatura_" + id + ".xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(data);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewInvoice(@PathVariable Long id) {
        byte[] data = invoiceReportService.exportInvoiceToPdf(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=preview_fatura_" + id + ".pdf")
                .header("Content-Type", "application/pdf")
                .body(data);
    }


}
