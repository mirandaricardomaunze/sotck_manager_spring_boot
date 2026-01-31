package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.OrderDTO;
import com.stock.stockmanager.dto.OrderItemDTO;
import com.stock.stockmanager.enums.OrderStatus;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.OrderMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CompanyRepository companyRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final OrderNumberGeneratorService orderNumberGeneratorService;

    public OrderService(OrderRepository orderRepository,
                        CompanyRepository companyRepository,
                        WarehouseRepository warehouseRepository,
                        ProductRepository productRepository,
                        StockRepository stockRepository,
                        OrderNumberGeneratorService orderNumberGeneratorService) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.orderNumberGeneratorService = orderNumberGeneratorService;
    }

    /** Criar pedido com reserva de estoque */
    public OrderDTO createOrder(OrderDTO dto) {
        if (dto == null) throw new BusinessException("Dados do pedido não podem ser nulos");
        if (dto.getItems() == null || dto.getItems().isEmpty()) throw new BusinessException("O pedido deve conter pelo menos um item");

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));
        List<Product> products = productRepository.findAll();

        // Geração de número da encomenda
        if (dto.getOrderNumber() == null || dto.getOrderNumber().isEmpty()) {
            dto.setOrderNumber(orderNumberGeneratorService.generateOrderNumber());
        }

        // Cria entidade do pedido
        Order order = OrderMapper.toEntity(dto, company, warehouse, products);

        // 🔹 Reserva estoque para cada item
        for (OrderItem item : order.getItems()) {
            Stock stock = stockRepository.findByProductAndWarehouse(item.getProduct(), warehouse)
                    .orElseThrow(() -> new BusinessException("Sem estoque do produto " + item.getProductName() + " no armazém selecionado"));

            int available = stock.getAvailableQuantity();
            if (available < item.getQuantity()) {
                throw new BusinessException("Estoque insuficiente para " + item.getProductName() + ": disponível " + available + ", solicitado " + item.getQuantity());
            }

            stock.reserveStock(item.getQuantity());
            stockRepository.save(stock);
        }

        order.setStatus(OrderStatus.PENDING); // status inicial
        Order saved = orderRepository.save(order);
        return OrderMapper.toDTO(saved);
    }

    /** Listar todos os pedidos */
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Listar pedidos por empresa */
    public List<OrderDTO> getOrdersByCompany(Long companyId) {
        return orderRepository.findByCompany_Id(companyId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Listar pedidos por armazém */
    public List<OrderDTO> getOrdersByWarehouse(Long warehouseId) {
        return orderRepository.findByWarehouse_Id(warehouseId).stream()
                .map(OrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /** Atualizar pedido existente */
    public OrderDTO updateOrder(Long id, OrderDTO dto) {
        if (dto == null) throw new BusinessException("Dados do pedido não podem ser nulos");
        if (dto.getItems() == null || dto.getItems().isEmpty()) throw new BusinessException("O pedido deve conter pelo menos um item para atualizar");

        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));
        List<Product> products = productRepository.findAll();

        // Atualiza o pedido
        Order updated = OrderMapper.toEntity(dto, company, warehouse, products);
        updated.setId(existing.getId());
        updated.setOrderNumber(existing.getOrderNumber());
        updated.setStatus(existing.getStatus());

        Order saved = orderRepository.save(updated);
        return OrderMapper.toDTO(saved);
    }

    /** Anular pedido e liberar estoque reservado */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        if(order.getStatus() == OrderStatus.CANCELED) return; // já anulado

        // Liberar estoque reservado
        for (OrderItem item : order.getItems()) {
            Stock stock = stockRepository.findByProductAndWarehouse(item.getProduct(), order.getWarehouse())
                    .orElseThrow(() -> new BusinessException("Produto " + item.getProductName() + " não tem estoque no armazém."));
            stock.releaseStock(item.getQuantity());
            stockRepository.save(stock);
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }
}
