package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.InvoiceDTO;
import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.enums.OrderStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.InvoiceMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.InvoiceRepository;
import com.stock.stockmanager.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final StockService stockService;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            OrderRepository orderRepository,
            StockService stockService
    ) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.stockService = stockService;
    }


    // =======================================================
    // 1️⃣ Criar fatura a partir do número do pedido
    // =======================================================
    @Transactional
    public InvoiceDTO createInvoiceFromOrderNumber(String orderNumber) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pedido não encontrado: " + orderNumber)
                );

        // Validar status do pedido
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new BusinessException("Não é possível criar fatura de um pedido CANCELADO.");
        }
        if (order.getStatus() == OrderStatus.DRAFT) {
            throw new BusinessException("O pedido ainda está em rascunho. Finalize antes de faturar.");
        }

        // Evitar duplicação
        if (invoiceRepository.existsByOrder_OrderNumber(orderNumber)) {
            throw new BusinessException("Já existe uma fatura para este pedido.");
        }

        // Validar estoque antes de tudo
        for (OrderItem item : order.getItems()) {
            Stock stock = stockService.getStockByProductAndWarehouse(
                    item.getProduct(),
                    order.getWarehouse()
            );

            if (stock.getQuantity() < item.getQuantity()) {
                throw new BusinessException(
                        "Estoque insuficiente para o produto: " + item.getProduct().getName()
                );
            }
        }

        // 1) Criar a fatura
        Invoice invoice = InvoiceMapper.fromOrder(order);
        invoice.setStatus(InvoiceStatus.INVOICED);
        Invoice savedInvoice = invoiceRepository.save(invoice);

        // 2) Deduzir estoque ‘item’ por item
        for (OrderItem item : order.getItems()) {
            stockService.decreaseStock(
                    item.getProduct().getId(),
                    order.getWarehouse().getId(),
                    item.getQuantity()
            );
        }

        // 3) Atualizar pedido
        order.setStatus(OrderStatus.INVOICED);
        orderRepository.save(order);

        return InvoiceMapper.toDTO(savedInvoice);
    }


    // =======================================================
    // 2️⃣ Listar todas as faturas
    // =======================================================
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }


    // =======================================================
    // 3️⃣ Buscar por número do pedido
    // =======================================================
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByOrderNumber(String orderNumber) {
        return invoiceRepository.findByOrder_OrderNumber(orderNumber)
                .stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }


    // =======================================================
    // 4️⃣ Buscar por ID
    // =======================================================
    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(InvoiceMapper::toDTO);
    }


    // =======================================================
    // 5️⃣ Buscar por status
    // =======================================================
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByStatus(String status) {
        InvoiceStatus st = parseStatus(status);

        return invoiceRepository.findByStatus(st).stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }


    // =======================================================
    // 6️⃣ Atualizar status da fatura
    // =======================================================
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long id, String status) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Fatura não encontrada com ID: " + id)
                );

        invoice.setStatus(parseStatus(status));
        Invoice updated = invoiceRepository.save(invoice);

        return InvoiceMapper.toDTO(updated);
    }


    // =======================================================
    // 7️⃣ Eliminar fatura
    // =======================================================
    @Transactional
    public void deleteInvoice(Long id) {

        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Fatura não encontrada.");
        }

        invoiceRepository.deleteById(id);
    }


    // =======================================================
    // 8️⃣ Cancelar fatura + devolver estoque
    // =======================================================
    @Transactional
    public InvoiceDTO cancelInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada."));

        if (invoice.getStatus() == InvoiceStatus.CANCELED) {
            throw new BusinessException("A fatura já está cancelada.");
        }

        Order order = invoice.getOrder();
        if (order == null) {
            throw new BusinessException("A fatura não está associada a um pedido.");
        }

        // Devolver estoque item a item
        for (InvoiceItem item : invoice.getItems()) {

            stockService.increaseStock(
                    item.getProduct().getId(),
                    order.getWarehouse().getId(),
                    item.getQuantity()
            );
        }

        // Atualizar status da fatura
        invoice.setStatus(InvoiceStatus.CANCELED);

        // Atualizar pedido
        order.setStatus(OrderStatus.CANCELED);

        invoiceRepository.save(invoice);
        orderRepository.save(order);

        return InvoiceMapper.toDTO(invoice);
    }


    // =======================================================
    // Conversão String → ENUM
    // =======================================================
    private InvoiceStatus parseStatus(String status) {
        try {
            return InvoiceStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new BusinessException("Status inválido: " + status);
        }
    }
}
