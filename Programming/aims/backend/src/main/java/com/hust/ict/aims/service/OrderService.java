package com.hust.ict.aims.service;

import com.hust.ict.aims.model.Orders;
import java.util.List;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All methods are strictly concerned with the persistence and retrieval of Order entities.
// There is no unrelated logic such as order validation, pricing calculation, or controller behavior.
//
// SRP COMMENT:
// Single Responsibility: This service handles only basic CRUD operations for Order entities.
// It does not manage inventory, user interactions, or business rules outside repository delegation.
// ------------------------------------------------------------

public interface OrderService {
    List<Orders> findAll();
    Orders findById(Long id);
    Orders save(Orders order);
    void deleteById(Long id);
    Orders updateOrderStatus(Long id, String status);
    /**
     * Find and reject all pending orders that request products with insufficient stock
     *
     * @return List of order IDs that were auto-rejected
     */
    List<Long> rejectOrdersWithInsufficientStock();
}
