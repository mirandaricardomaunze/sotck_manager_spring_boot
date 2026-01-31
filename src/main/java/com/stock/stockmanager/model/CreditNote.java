package com.stock.stockmanager.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "credit_notes")
public class CreditNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String creditNoteNumber;       // Número único da nota de crédito
    private LocalDateTime creditNoteDate;  // Data de emissão
    private BigDecimal totalAmount;        // Valor total a creditar

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;               // Fatura relacionada

    @OneToMany(mappedBy = "creditNote", cascade = CascadeType.ALL)
    private List<CreditNoteItem> items;    // Itens estornados
    @ManyToOne
    @JoinColumn(name = "company_id") // garante multi-company
    private Company company;

    private String notes;                  // Observações

    @PrePersist
    public void prePersist() {
        if (this.creditNoteNumber == null || this.creditNoteNumber.isEmpty()) {
            this.creditNoteNumber = "CN-" + System.currentTimeMillis();
        }
        if (this.creditNoteDate == null) {
            this.creditNoteDate = LocalDateTime.now();
        }
    }
}
