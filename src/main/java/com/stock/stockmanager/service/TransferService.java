package com.stock.stockmanager.service;

import com.stock.stockmanager.dto.TransferRequestDTO;
import com.stock.stockmanager.dto.TransferResponseDTO;
import com.stock.stockmanager.exception.BusinessException;
import com.stock.stockmanager.exception.ResourceNotFoundException;
import com.stock.stockmanager.mapper.TransferMapper;
import com.stock.stockmanager.model.*;
import com.stock.stockmanager.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final TransferMapper transferMapper;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    // ============================================================
    // CREATE TRANSFER
    // ============================================================
    @Transactional
    public TransferResponseDTO createTransfer(TransferRequestDTO dto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (dto.getProductId() == null) {
            throw new BusinessException("Produto ID não pode ser nulo");
        }
        if (dto.getSourceWarehouseId() == null) {
            throw new BusinessException("Armazém de origem ID não pode ser nulo");
        }
        if (dto.getDestinationWarehouseId() == null) {
            throw new BusinessException("Armazém de destino ID não pode ser nulo");
        }
        if (dto.getCompanyId() == null) {
            throw new BusinessException("Empresa ID não pode ser nula");
        }
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero");
        }
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        Warehouse warehouseOrigin = warehouseRepository.findById(dto.getSourceWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém de origem não encontrado"));

        Warehouse warehouseDestination = warehouseRepository.findById(dto.getDestinationWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Armazém de destino não encontrado"));

        if (warehouseOrigin.getId().equals(warehouseDestination.getId())) {
            throw new BusinessException("Armazém de origem e destino não podem ser iguais");
        }

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        Stock stockOrigin = stockRepository.findByProductAndWarehouse(product, warehouseOrigin)
                .orElseThrow(() -> new BusinessException("Produto não possui estoque no armazém de origem"));

        Stock stockDestination = stockRepository.findByProductAndWarehouse(product, warehouseDestination)
                .orElseGet(() -> Stock.builder()
                        .product(product)
                        .warehouse(warehouseDestination)
                        .quantity(0)
                        .build());

        if (stockOrigin.getQuantity() < dto.getQuantity()) {
            throw new BusinessException("Estoque insuficiente para transferência");
        }

        // Atualizar estoques
        stockOrigin.setQuantity(stockOrigin.getQuantity() - dto.getQuantity());
        stockDestination.setQuantity(stockDestination.getQuantity() + dto.getQuantity());

        stockRepository.save(stockOrigin);
        stockRepository.save(stockDestination);

        Transfer transfer = Transfer.builder()
                .company(company)
                .product(product)
                .sourceWarehouse(warehouseOrigin)
                .destinationWarehouse(warehouseDestination)
                .quantity(dto.getQuantity())
                .transferDate(LocalDateTime.now())
                .reference(dto.getReference())
                .user(user)
                .build();

        transferRepository.save(transfer);

        return transferMapper.toResponseDTO(transfer, stockOrigin, stockDestination);
    }

    // ============================================================
    // UPDATE TRANSFER
    // ============================================================
    @Transactional
    public TransferResponseDTO updateTransfer(Long id, TransferRequestDTO dto) {

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transferência não encontrada"));

        Stock stockOrigin = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getSourceWarehouse()
        ).orElseThrow(() -> new BusinessException("Estoque de origem não encontrado"));

        Stock stockDestination = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getDestinationWarehouse()
        ).orElseThrow(() -> new BusinessException("Estoque de destino não encontrado"));

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero");
        }

        // Reverter quantidade antiga
        stockOrigin.setQuantity(stockOrigin.getQuantity() + transfer.getQuantity());
        stockDestination.setQuantity(stockDestination.getQuantity() - transfer.getQuantity());

        if (stockOrigin.getQuantity() < dto.getQuantity()) {
            throw new BusinessException("Estoque insuficiente para transferência atualizada");
        }

        // Aplicar nova quantidade
        stockOrigin.setQuantity(stockOrigin.getQuantity() - dto.getQuantity());
        stockDestination.setQuantity(stockDestination.getQuantity() + dto.getQuantity());

        stockRepository.save(stockOrigin);
        stockRepository.save(stockDestination);

        transfer.setQuantity(dto.getQuantity());
        transfer.setReference(dto.getReference());

        transferRepository.save(transfer);

        return transferMapper.toResponseDTO(transfer, stockOrigin, stockDestination);
    }

    // ============================================================
    // DELETE TRANSFER
    // ============================================================
    @Transactional
    public void deleteTransfer(Long id) {

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transferência não encontrada"));

        Stock stockOrigin = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getSourceWarehouse()
        ).orElseThrow(() -> new BusinessException("Estoque de origem não encontrado"));

        Stock stockDestination = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getDestinationWarehouse()
        ).orElseThrow(() -> new BusinessException("Estoque de destino não encontrado"));

        // Reverter estoque
        stockOrigin.setQuantity(stockOrigin.getQuantity() + transfer.getQuantity());
        stockDestination.setQuantity(stockDestination.getQuantity() - transfer.getQuantity());

        stockRepository.save(stockOrigin);
        stockRepository.save(stockDestination);

        transferRepository.delete(transfer);
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    public TransferResponseDTO getById(Long id) {

        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transferência não encontrada"));

        Stock stockOrigin = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getSourceWarehouse()
        ).orElse(null);

        Stock stockDestination = stockRepository.findByProductAndWarehouse(
                transfer.getProduct(),
                transfer.getDestinationWarehouse()
        ).orElse(null);

        return transferMapper.toResponseDTO(transfer, stockOrigin, stockDestination);
    }

    // ============================================================
    // LIST ALL
    // ============================================================
    public List<TransferResponseDTO> getAll() {

        return transferRepository.findAll().stream().map(t -> {
            Stock origin = stockRepository.findByProductAndWarehouse(
                    t.getProduct(),
                    t.getSourceWarehouse()
            ).orElse(null);

            Stock destination = stockRepository.findByProductAndWarehouse(
                    t.getProduct(),
                    t.getDestinationWarehouse()
            ).orElse(null);

            return transferMapper.toResponseDTO(t, origin, destination);
        }).toList();
    }
}
