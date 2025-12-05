package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.InvoiceDTO;
import com.stock.stockmanager.enums.InvoiceStatus;
import com.stock.stockmanager.enums.OrderStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.InvoiceMapper;
import com.stock.stockmanager.model.Invoice;
import com.stock.stockmanager.model.Order;
import com.stock.stockmanager.model.OrderItem;
import com.stock.stockmanager.repository.InvoiceRepository;
import com.stock.stockmanager.repository.OrderRepository;
import com.stock.stockmanager.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
        private final ProductRepository productRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.productRepository=productRepository;
    }

    @Transactional
    public InvoiceDTO createInvoiceFromOrderNumber(String orderNumber) {
        System.out.println("Tentando criar fatura para o pedido: " + orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> {
                    System.out.println("Pedido não encontrado com número: " + orderNumber);
                    return new IllegalArgumentException("Pedido não encontrado com número: " + orderNumber);
                });
        if (invoiceRepository.existsByOrder_OrderNumber(orderNumber)) {
            System.out.println("Já existe uma fatura para o pedido: " + orderNumber);
            throw new IllegalStateException("Já existe uma fatura para o pedido: " + orderNumber);
        }

        // 3️⃣ Validar estoque
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null) {
                System.out.println("Item do pedido sem produto associado: " + item.getId());
                throw new ResourceNotFoundException("Item do pedido sem produto associado: " + item.getId());
            }
            if (item.getQuantity() > item.getProduct().getQuantityInStock()) {
                System.out.println("Estoque insuficiente para o produto: " + item.getProduct().getName());
                throw new BusinessException("Estoque insuficiente para o produto: " + item.getProduct().getName());
            }
        }

        // 4️⃣ Criar fatura
        Invoice invoice = InvoiceMapper.fromOrder(order);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        System.out.println("Fatura criada com sucesso: " + savedInvoice.getInvoiceNumber());

        // 5️⃣ Atualizar estoque
        for (OrderItem item : order.getItems()) {
            item.getProduct().setQuantityInStock(item.getProduct().getQuantityInStock() - item.getQuantity());
            productRepository.save(item.getProduct());
        }
        order.setStatus(OrderStatus.INVOICED);
        orderRepository.save(order);
        System.out.println("Estoque atualizado para o pedido: " + orderNumber);

        return InvoiceMapper.toDTO(savedInvoice);
    }

    /** Retorna todas as faturas */
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Retorna as faturas de um pedido específico */
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByOrderNumber(String orderNumber) {
        return invoiceRepository.findByOrder_OrderNumber(orderNumber)
                .stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Busca uma fatura pelo ID */
    @Transactional(readOnly = true)
    public Optional<InvoiceDTO> getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .map(InvoiceMapper::toDTO);
    }

     /**
     * Busca faturas por status (PAGO, PENDENTE, CANCELADO)
     */
    @Transactional(readOnly = true)
    public List<InvoiceDTO> getInvoicesByStatus(String status) {
        System.out.println("Buscando faturas com status: " + status);

        // Validar status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Status inválido: " + status + ". Status válidos: PAGO, PENDENTE, CANCELADO");
        }

        List<InvoiceDTO> invoices = invoiceRepository.findByStatus(InvoiceStatus.valueOf(status))
                .stream()
                .map(InvoiceMapper::toDTO)
                .collect(Collectors.toList());

        System.out.println("Encontradas " + invoices.size() + " faturas com status: " + status);
        return invoices;
    }

    /**
     * Atualiza o status de uma fatura
     */
    @Transactional
    public InvoiceDTO updateInvoiceStatus(Long id, String status) {
        System.out.println("Atualizando status da fatura ID: " + id + " para: " + status);

        // Validar status
        if (!isValidStatus(status)) {
            throw new ResourceNotFoundException("Status inválido: " + status + ". Status válidos: PAGO, PENDENTE, CANCELADO");
        }

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> {
                    System.out.println("Fatura não encontrada com ID: " + id);
                    return new ResourceNotFoundException("Fatura não encontrada com ID: " + id);
                });

        String oldStatus = invoice.getStatus().name();
        invoice.setStatus(InvoiceStatus.valueOf(status.toUpperCase()));

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        System.out.println("Status da fatura " + id + " atualizado de '" + oldStatus + "' para '" + status + "'");

        return InvoiceMapper.toDTO(updatedInvoice);
    }

    /**
     * Exclui uma fatura
     */
    @Transactional
    public void deleteInvoice(Long id) {
        System.out.println("Tentando excluir fatura com ID: " + id);

        if (!invoiceRepository.existsById(id)) {
            System.out.println("Fatura não encontrada para exclusão: " + id);
            throw new ResourceNotFoundException("Fatura não encontrada com ID: " + id);
        }

        invoiceRepository.deleteById(id);
        System.out.println("Fatura excluída com sucesso: " + id);
    }

    /**
     * Valida se o status é válido
     */
    private boolean isValidStatus(String status) {
        if (status == null) return false;

        String normalizedStatus = status.toUpperCase();
        return normalizedStatus.equals("PAID") ||
                normalizedStatus.equals("INVOICED") ||
                normalizedStatus.equals("PENDING") ||
                normalizedStatus.equals("CANCELED");
    }
}