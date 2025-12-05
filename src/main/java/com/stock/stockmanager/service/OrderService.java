package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.OrderDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.OrderMapper;
import com.stock.stockmanager.model.Company;
import com.stock.stockmanager.model.Order;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.repository.CompanyRepository;
import com.stock.stockmanager.repository.OrderRepository;
import com.stock.stockmanager.repository.ProductRepository;
import com.stock.stockmanager.repository.WarehouseRepository;
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
    private final OrderNumberGeneratorService orderNumberGeneratorService;

    public OrderService(OrderRepository orderRepository,
                        CompanyRepository companyRepository,
                        WarehouseRepository warehouseRepository,
                        ProductRepository productRepository,
                        OrderNumberGeneratorService orderNumberGeneratorService) {
        this.orderRepository = orderRepository;
        this.companyRepository = companyRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.orderNumberGeneratorService = orderNumberGeneratorService;
    }

    /** Criar pedido */
    public OrderDTO createOrder(OrderDTO dto) {
        if (dto == null) {
            throw new BusinessException("Dados do pedido não podem ser nulos");
        }

        // 🔹 Verifica se existem itens
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("O pedido deve conter pelo menos um item");
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));
        List<Product> products = productRepository.findAll();

        // 🔹 Geração garantida e sequencial de número da encomenda
        if (dto.getOrderNumber() == null || dto.getOrderNumber().isEmpty()) {
            dto.setOrderNumber(orderNumberGeneratorService.generateOrderNumber());
        }

        Order order = OrderMapper.toEntity(dto, company, warehouse, products);
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException("Erro: não é possível criar um pedido sem itens válidos.");
        }

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
        if (dto == null) {
            throw new BusinessException("Dados do pedido não podem ser nulos");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("O pedido deve conter pelo menos um item para atualizar");
        }

        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));
        List<Product> products = productRepository.findAll();

        Order updated = OrderMapper.toEntity(dto, company, warehouse, products);
        updated.setId(existing.getId());
        updated.setOrderNumber(existing.getOrderNumber());

        Order saved = orderRepository.save(updated);
        return OrderMapper.toDTO(saved);
    }

    /** Apagar pedido */
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido não encontrado");
        }
        orderRepository.deleteById(id);
    }
}
