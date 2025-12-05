package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.StockDTO;
import com.stock.stockmanager.dto.StockRequestDTO;
import com.stock.stockmanager.dto.StockSummaryDTO;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.StockMapper;
import com.stock.stockmanager.model.Product;
import com.stock.stockmanager.model.Stock;
import com.stock.stockmanager.model.Warehouse;
import com.stock.stockmanager.repository.ProductRepository;
import com.stock.stockmanager.repository.StockRepository;
import com.stock.stockmanager.repository.WarehouseRepository;
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

    // =============================
    // MÉTODOS ANTIGOS
    // =============================
    public List<StockDTO> getAll() {
        return stockRepository.findAll().stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    public List<StockDTO> getAllByWarehouse(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        return stockRepository.findAllByWarehouse(warehouse).stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    public List<StockDTO> getAllByProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        return stockRepository.findAllByProduct(product).stream()
                .map(stockMapper::toDTO)
                .toList();
    }

    public StockDTO createOrUpdate(StockRequestDTO dto) {

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém não encontrado"));

        // Verificar se já existe ‘estoque’ deste produto neste armazém
        Stock stock = stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElse(Stock.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build()
                );

        // Atualiza quantidade
        stock.setQuantity(stock.getQuantity() + dto.getQuantity());

        Stock saved = stockRepository.save(stock);
        return stockMapper.toDTO(saved);
    }


    // =============================
    // NOVOS MÉTODOS DE SUMÁRIO
    // =============================
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
