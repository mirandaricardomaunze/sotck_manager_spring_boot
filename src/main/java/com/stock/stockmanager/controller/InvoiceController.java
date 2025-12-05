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

    // --- ENDPOINTS EXISTENTES (MANTIDOS) ---
    @PostMapping("/create/{orderNumber}")
    public ResponseEntity<InvoiceDTO> createInvoice(@PathVariable String orderNumber) {
        InvoiceDTO invoice = invoiceService.createInvoiceFromOrderNumber(orderNumber);
        return ResponseEntity.status(201).body(invoice);
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDTO>> getAllInvoices() {
        List<InvoiceDTO> invoices = invoiceService.getAllInvoices();
        return invoices.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(invoices);
    }

    @GetMapping("/order/{orderNumber}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByOrderNumber(@PathVariable String orderNumber) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByOrderNumber(orderNumber);
        if (invoices.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma fatura encontrada para o pedido: " + orderNumber);
        }
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada com ID: " + id));
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportInvoicePdf(@PathVariable Long id) {
        try {
            byte[] data = invoiceReportService.exportInvoiceToPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=fatura_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao exportar PDF: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportInvoiceExcel(@PathVariable Long id) {
        try {
            byte[] data = invoiceReportService.exportInvoiceToExcel(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=fatura_" + id + ".xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao exportar Excel: " + e.getMessage()).getBytes());
        }
    }

    // --- NOVOS ENDPOINTS ADICIONADOS ---

    @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewInvoice(@PathVariable Long id) {
        try {
            byte[] data = invoiceReportService.exportInvoiceToPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=preview_fatura_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao visualizar fatura: " + e.getMessage()).getBytes());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InvoiceDTO>> getInvoicesByStatus(@PathVariable String status) {
        List<InvoiceDTO> invoices = invoiceService.getInvoicesByStatus(status);
        if (invoices.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma fatura encontrada com status: " + status);
        }
        return ResponseEntity.ok(invoices);
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<InvoiceDTO> updateInvoiceStatus(@PathVariable Long id, @PathVariable String status) {
        InvoiceDTO updatedInvoice = invoiceService.updateInvoiceStatus(id, status);
        return ResponseEntity.ok(updatedInvoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/html")
    public ResponseEntity<String> exportInvoiceHtml(@PathVariable Long id) {
        try {
            // Assumindo que você vai adicionar este método no service
            byte[] htmlData = invoiceReportService.exportInvoiceToPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .body(new String(htmlData));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body("Falha ao exportar HTML: " + e.getMessage());
        }
    }
}