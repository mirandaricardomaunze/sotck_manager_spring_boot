package com.stock.stockmanager.controller;
import com.stock.stockmanager.dto.CreditNoteDTO;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.CreditNoteItem;
import com.stock.stockmanager.service.CreditNoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/credit-notes")
public class CreditNoteController {

    private final CreditNoteService creditNoteService;
    //private final CreditNoteReportService creditNoteReportService;

    public CreditNoteController(CreditNoteService creditNoteService) {
        this.creditNoteService = creditNoteService;
    }

    /** Cria uma nota de crédito para uma fatura */
    @PostMapping("/create/{invoiceId}")
    public ResponseEntity<CreditNoteDTO> createCreditNote(
            @PathVariable Long invoiceId,
            @RequestBody List<CreditNoteItem> items,
            @RequestParam Long companyId) {

        // Você precisará buscar a empresa pelo ID, ou receber o objeto diretamente
        Company company = new Company();
        company.setId(companyId); // simplificado, ideal buscar do DB

        CreditNoteDTO creditNote = creditNoteService.createCreditNote(invoiceId, items, company);
        return ResponseEntity.status(201).body(creditNote);
    }

    @GetMapping
    public ResponseEntity<List<CreditNoteDTO>> getAllCreditNotes() {
        List<CreditNoteDTO> notes = creditNoteService.getAllCreditNotes();
        return notes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notes);
    }


    /** Lista todas as notas de crédito de uma fatura */
    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<List<CreditNoteDTO>> getByInvoice(@PathVariable Long invoiceId) {
        List<CreditNoteDTO> notes = creditNoteService.getByInvoice(invoiceId);
        return notes.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(notes);
    }
/*
    // --- EXPORTAR PDF ---
    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportCreditNotePdf(@PathVariable Long id) {
        try {
            byte[] data = creditNoteReportService.exportCreditNoteToPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=credit_note_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao exportar PDF: " + e.getMessage()).getBytes());
        }
    }
   */
    // --- EXPORTAR EXCEL ---
    /*
    @GetMapping("/{id}/export/excel")
    public ResponseEntity<byte[]> exportCreditNoteExcel(@PathVariable Long id) {
        try {
            byte[] data = creditNoteReportService.exportCreditNoteToExcel(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=credit_note_" + id + ".xlsx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao exportar Excel: " + e.getMessage()).getBytes());
        }
    }
    */


    // --- VISUALIZAR NOTA DE CRÉDITO (PREVIEW PDF) ---
   /*
   @GetMapping("/{id}/preview")
    public ResponseEntity<byte[]> previewCreditNote(@PathVariable Long id) {
        try {
            byte[] data = creditNoteReportService.exportCreditNoteToPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=preview_credit_note_" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .header("Content-Type", "text/plain")
                    .body(("Falha ao visualizar nota de crédito: " + e.getMessage()).getBytes());
        }
    }
    */
}
