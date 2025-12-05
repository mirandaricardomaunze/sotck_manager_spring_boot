package com.stock.stockmanager.service;

import com.stock.stockmanager.model.Order;
import com.stock.stockmanager.model.OrderSequence;
import com.stock.stockmanager.repository.OrderSequenceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderNumberGeneratorService {

    private final OrderSequenceRepository repository;

    public OrderNumberGeneratorService(OrderSequenceRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public synchronized String generateOrderNumber() {
        OrderSequence sequence = repository.findById(1L)
                .orElseGet(() -> {
                    OrderSequence newSeq = new OrderSequence();
                    newSeq.setNextValue(1L);
                    return repository.save(newSeq);
                });

        Long next = sequence.getNextValue();
        sequence.setNextValue(next + 1);
        repository.save(sequence);
        return String.format("ORD-%05d", next);
    }

}
