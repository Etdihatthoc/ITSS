package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.model.CartItem;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.repository.OrderRepository;
import com.hust.ict.aims.model.Orders;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
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

    @Override
    @Transactional
    public Orders updateOrderStatus(Long id, String status) {
        // Find the order
        Orders order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        // Validate status transition
        validateStatusTransition(order.getStatus(), status);

        // Update status
        order.setStatus(status);

        // Save and return
        return orderRepository.save(order);
    }

    /**
     * Validate if the status transition is allowed
     * @param currentStatus Current order status
     * @param newStatus New order status
     */
    private void validateStatusTransition(String currentStatus, String newStatus) {
        boolean isValid = false;

        switch (currentStatus) {
            case "PENDING":
                isValid = newStatus.equals("APPROVED") || newStatus.equals("REJECTED");
                break;
            case "APPROVED":
                isValid = newStatus.equals("SHIPPED") || newStatus.equals("CANCELLED");
                break;
            case "SHIPPED":
                isValid = newStatus.equals("DELIVERED");
                break;
            case "DELIVERED":
            case "REJECTED":
            case "CANCELLED":
                isValid = false; // Terminal states
                break;
            default:
                throw new IllegalArgumentException("Unknown current status: " + currentStatus);
        }

        if (!isValid) {
            throw new IllegalStateException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus
            );
        }
    }

    @Override
    @Transactional
    public List<Long> rejectOrdersWithInsufficientStock() {
        // Find all pending orders
        List<Orders> pendingOrders = orderRepository.findByStatus("PENDING");
        List<Long> rejectedOrderIds = new ArrayList<>();

        // Check each pending order
        for (Orders order : pendingOrders) {
            boolean hasInsufficientItems = false;
            StringBuilder insufficientItems = new StringBuilder();

            // Check each item in the order
            for (CartItem item : order.getInvoice().getCart().getItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getId()));

                // If requested quantity exceeds available stock
                if (item.getQuantity() > product.getQuantity()) {
                    hasInsufficientItems = true;
                    insufficientItems.append(product.getTitle())
                            .append(" (requested: ")
                            .append(item.getQuantity())
                            .append(", available: ")
                            .append(product.getQuantity())
                            .append("), ");
                }
            }

            // Reject the order if any items have insufficient stock
            if (hasInsufficientItems) {
                // Update status to REJECTED
                order.setStatus("REJECTED");
                orderRepository.save(order);

                // Add rejection note
                String reason = "Order automatically rejected due to insufficient stock: " +
                        insufficientItems.substring(0, insufficientItems.length() - 2);

                rejectedOrderIds.add(order.getId());
            }
        }
        return rejectedOrderIds;
    }
}
