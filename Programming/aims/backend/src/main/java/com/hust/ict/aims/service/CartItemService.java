package com.hust.ict.aims.service;

import com.hust.ict.aims.model.CartItem;

import java.util.List;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All methods focus on persistence and basic operations for CartItem entities.
// The class contains no unrelated logic such as cart total calculation, promotions, or session handling.
//
// SRP COMMENT:
// Single Responsibility: This service is dedicated to handling CRUD and query operations for CartItem.
// It delegates persistence to CartItemRepository and avoids incorporating other business rules or features.
// ------------------------------------------------------------

public interface CartItemService {
    CartItem save(CartItem cartItem);
    CartItem findById(Long id);
    List<CartItem> findAll();
    CartItem update(Long id, CartItem cartItem);
    boolean delete(Long id);
    List<CartItem> findByCartId(Long cartId);
}
