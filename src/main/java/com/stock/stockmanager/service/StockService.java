package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.StockResponseDTO;
import com.stock.stockmanager.dto.StockRequestDTO;
import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.StockMapper;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Stock;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.repository.ProductRepository;
import com.stock.stockmanager.repository.StockRepository;
import com.stock.stockmanager.repository.WarehouseRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockMapper stockMapper;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;

    // ==========================================
    // LISTAR / BUSCAR / CONSULTAR
    // ==========================================
    public List<StockResponseDTO> getAll() {
        return stockRepository.findAll().stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    @Transactional
    public Stock getStockByProductAndWarehouse(Product product, Warehouse warehouse) {
        return stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() ->
                        new BusinessException("Sem estoque de " + product.getName()
                                + " no armazém " + warehouse.getName())
                );
    }

    public List<StockResponseDTO> getAllByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        return stockRepository.findAllByWarehouse(warehouse).stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    public List<StockResponseDTO> getAllByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return stockRepository.findAllByProduct(product).stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    // ==========================================
    // CRIAR OU ATUALIZAR ESTOQUE (ENTRADA)
    // ==========================================
    public StockResponseDTO createOrUpdate(StockRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(Stock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build()
                );

        stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        Stock saved = stockRepository.save(stock);
        return stockMapper.toDTO(saved);
    }

    // ==========================================
    // SAVE
    // ==========================================
    @Transactional
    public Stock save(Stock stock) {
        if (stock == null)
            throw new IllegalArgumentException("Stock não pode ser nulo");

        if (stock.getQuantity() < 0)
            throw new BusinessException("Quantidade de estoque não pode ser negativa");

        return stockRepository.save(stock);
    }

    // ==========================================
    // **INCREMENTAR ESTOQUE** (REPOSIÇÃO/DEVOLUÇÃO)
    // ==========================================
    @Transactional
    public void addStock(Product product, Warehouse warehouse, int quantityToAdd) {

        if (quantityToAdd <= 0)
            throw new BusinessException("A quantidade adicionada deve ser maior que zero");

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(Stock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        stock.setQuantity(stock.getQuantity() + quantityToAdd);
        stockRepository.save(stock);
    }

    // ==========================================
// ✔️ NOVO — increaseStock (compatível com InvoiceService)
// ==========================================
    @Transactional
    public void increaseStock(Long productId, Long warehouseId, Integer quantity) {

        if (quantity == null || quantity <= 0)
            throw new BusinessException("A quantidade adicionada deve ser maior que zero.");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(Stock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        stock.setQuantity(stock.getQuantity() + quantity);
        stockRepository.save(stock);
    }


    // ==========================================
    // ❗❗ **NOVO — DECREMENTAR ESTOQUE** (FATURAÇÃO / SAÍDA)
    // ==========================================
    @Transactional
    public void decreaseStock(Long productId, Long warehouseId, Integer quantity) {

        if (quantity == null || quantity <= 0)
            throw new BusinessException("A quantidade deve ser maior que zero");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new BusinessException(
                        "Produto " + product.getName() + " sem estoque no armazém " + warehouse.getName()
                ));

        if (stock.getQuantity() < quantity)
            throw new BusinessException(
                    "Estoque insuficiente de " + product.getName()
                            + ". Disponível: " + stock.getQuantity()
                            + ", solicitado: " + quantity
            );

        stock.setQuantity(stock.getQuantity() - quantity);
        stockRepository.save(stock);
    }

    // ==========================================
    // SUMÁRIOS
    // ==========================================
    public Long getTotalStockByProduct(Long productId) {
        Long total = stockRepository.sumQuantityByProduct(productId);
        return total != null ? total : 0L;
    }

    public Long getTotalStockByWarehouse(Long warehouseId) {
        Long total = stockRepository.sumQuantityByWarehouse(warehouseId);
        return total != null ? total : 0L;
    }

    public List<StockSummaryDTO> getSummaryByProduct() {
        return stockRepository.getStockSummaryByProduct();
    }

    public List<StockSummaryDTO> getSummaryByWarehouse() {
        return stockRepository.getStockSummaryByWarehouse();
    }
}
