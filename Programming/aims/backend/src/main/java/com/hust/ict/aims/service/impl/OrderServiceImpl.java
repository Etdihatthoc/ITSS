package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.DeliveryInfo;
import com.hust.ict.aims.model.Invoice;
import com.hust.ict.aims.model.Transaction;
import com.hust.ict.aims.repository.OrderRepository;
import com.hust.ict.aims.model.Orders;
import com.hust.ict.aims.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Orders> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public Orders findById(Long id) {
        Optional<Orders> result = orderRepository.findById(id);
        return result.orElseThrow(() -> new RuntimeException("Order not found - " + id));
    }

    @Override
    public Orders save(Orders order) {
        return orderRepository.save(order);
    }

    @Override
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
