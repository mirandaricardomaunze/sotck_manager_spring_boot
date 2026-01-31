package com.stock.stockmanager.service;
import com.stock.stockmanager.dto.CreditNoteDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.CreditNoteMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.CreditNoteRepository;
import com.stock.stockmanager.repository.InvoiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final InvoiceRepository invoiceRepository;
    private final StockService stockService;

    public CreditNoteService(CreditNoteRepository creditNoteRepository,
                             InvoiceRepository invoiceRepository,
                             StockService stockService) {
        this.creditNoteRepository = creditNoteRepository;
        this.invoiceRepository = invoiceRepository;
        this.stockService = stockService;
    }

    @Transactional
    public CreditNoteDTO createCreditNote(Long invoiceId, List<CreditNoteItem> items, Company company) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada: " + invoiceId));

        // Checagem de empresa
        if (!invoice.getCompanyName().equals(company)) {
            throw new BusinessException("Não é permitido criar nota de crédito para outra empresa");
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Nenhum item informado para a nota de crédito");
        }

        // Atualiza estoque e garante persistência
        for (CreditNoteItem item : items) {
            Stock stock = stockService.getStockByProductAndWarehouse(item.getProduct(), invoice.getOrder().getWarehouse());
            stock.setQuantity(stock.getQuantity() + item.getQuantity());
            stockService.save(stock);
            item.setWarehouse(invoice.getOrder().getWarehouse()); // mantém referência
        }

        // Cria nota de crédito
        CreditNote cn = CreditNoteMapper.fromInvoice(invoice, items);
        cn.setCompany(company); // associa empresa
        CreditNote saved = creditNoteRepository.save(cn);

        return CreditNoteMapper.toDTO(saved);
    }

    @Transactional
    public List<CreditNoteDTO> getAllCreditNotes() {
        return creditNoteRepository.findAll().stream()
                .map(CreditNoteMapper::toDTO)
                .toList();
    }


    @Transactional
    public List<CreditNoteDTO> getByInvoice(Long invoiceId) {
        return creditNoteRepository.findByInvoice_Id(invoiceId).stream()
                .map(CreditNoteMapper::toDTO)
                .toList();
    }
}
