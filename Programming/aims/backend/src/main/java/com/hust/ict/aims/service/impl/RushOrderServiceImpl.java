package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.RushOrder;
import com.hust.ict.aims.repository.RushOrderRepository;
import com.hust.ict.aims.service.RushOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
// Functional Cohesion, no violate SRP because it has only single responsibility: perform CRUD on RushOrder
@Service
public class RushOrderServiceImpl implements RushOrderService {
    private final RushOrderRepository rushOrderRepository;

    public RushOrderServiceImpl(RushOrderRepository rushOrderRepository) {
        this.rushOrderRepository = rushOrderRepository;
    }

    @Override
    public List<RushOrder> findAll() {
        return rushOrderRepository.findAll();
    }

    @Override
    public RushOrder findById(Long id) {
        Optional<RushOrder> result =  rushOrderRepository.findById(id);
        return result.orElseThrow(() -> new RuntimeException("RushOrder not found"));
    }

    @Override
    public RushOrder save(RushOrder rushOrder) {
        return rushOrderRepository.save(rushOrder);
    }

    @Override
    public void deleteById(Long id) {
        rushOrderRepository.deleteById(id);
    }
}
