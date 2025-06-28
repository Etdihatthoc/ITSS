package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.CartItemDTO;
import com.hust.ict.aims.dto.CartRequestDTO;
import com.hust.ict.aims.dto.DeliveryInfoDTO;
import com.hust.ict.aims.model.*;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.repository.RushOrderRepository;
import com.hust.ict.aims.service.RushOrderService;
import com.hust.ict.aims.config.Config;
import org.springframework.stereotype.Service;

import java.util.*;

// Functional Cohesion, no violate SRP because it has only single responsibility: perform CRUD on RushOrder
@Service
public class RushOrderServiceImpl implements RushOrderService {
    private final RushOrderRepository rushOrderRepository;
    private final ProductRepository productRepository;

    public RushOrderServiceImpl(RushOrderRepository rushOrderRepository, ProductRepository productRepository) {
        this.rushOrderRepository = rushOrderRepository;
        this.productRepository = productRepository;
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

    @Override
    public boolean isSupportedAddress(DeliveryInfoDTO deliveryInfoDTO) {
        if(!deliveryInfoDTO.getProvince().equalsIgnoreCase("hanoi")) {
            return false;
        }
        return Config.INNER_DISTRICTS.contains(deliveryInfoDTO.getDistrict());
    }

    @Override
    public boolean isAnySupportedItem(CartRequestDTO cartRequestDTO) {
        Set<Long> productIds = new HashSet<>();
        for(CartItemDTO cartItemDTO : cartRequestDTO.getItems()) {
            productIds.add(cartItemDTO.getProductId());
        }
        List<Product> products = productRepository.findByIdIn(productIds);
        for (Product product : products) {
            if(product.isRushOrderEligible()) {
                return true;
            }
        }
        return false;
    }

}
