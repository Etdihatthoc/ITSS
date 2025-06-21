package com.hust.ict.aims.service;
import com.hust.ict.aims.dto.CartItemRequestDTO;
import com.hust.ict.aims.dto.CartItemsRequest;
import com.hust.ict.aims.dto.CartRequest;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.model.CartItem;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: This class is focused entirely on cart management—
// creating, updating, finding, and modifying cart contents.
//
// SRP COMMENT:
// Single Responsibility: This service is responsible only for cart-related use cases.
// It doesn’t handle checkout, payment, or delivery logic.
// All business logic is scoped to cart creation and modification.
// ------------------------------------------------------------
import java.util.List;
import java.util.Map;
public interface CartService {
    Cart emptyCart(Long cartId);
    Cart save(Cart cart);
    Cart findById(Long id);
    List<Cart> findAll();
    Cart update(Long id, Cart cart);
    boolean delete(Long id);
    Cart addItemToCart(Long cart, CartItemRequestDTO cartItemRequestDTO);
    Cart removeItemFromCart(Long cartId, Long productId);
    Cart createEmptyCart();
    float calculateFinalAmount(Cart cart, float vatRate, float deliveryFee);
    float calculateShippingDiscount(Cart cart, float deliveryFee);
    boolean isEligibleForFreeShipping(Cart cart);
    float calculateTotalPriceIncludingVAT(Cart cart, float vatRate);
    List<CartItem> getInsufficientItems(Cart cart);
    boolean hasSufficientInventory(Cart cart);
    float calculateTotalBeforeVAT(Cart cart);
    Cart updateItemQuanity(Long cartId, Long productId, Integer newQuantity);
    Map<String, Object> checkInventoryForCart(Long cartId);
    Cart createCartSnapshot(CartRequest cartData);
}
