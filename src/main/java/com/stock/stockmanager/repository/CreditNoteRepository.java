package com.stock.stockmanager.repository;

import com.stock.stockmanager.model.CreditNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditNoteRepository extends JpaRepository<CreditNote, Long> {
    List<CreditNote> findByInvoice_Id(Long invoiceId);
}
