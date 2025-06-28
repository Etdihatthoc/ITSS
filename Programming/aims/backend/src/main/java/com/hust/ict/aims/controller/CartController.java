package com.hust.ict.aims.controller;

import com.hust.ict.aims.dto.CartCalculationRequestDTO;
import com.hust.ict.aims.dto.CartCalculationResponseDTO;
import com.hust.ict.aims.dto.CartItemRequestDTO;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carts")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create")
    public ResponseEntity<Cart> createCart() {
        return ResponseEntity.ok(cartService.createEmptyCart());
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<?> addItemToCart(@PathVariable Long cartId, @RequestBody CartItemRequestDTO itemDTO) {
        try {
            Cart updatedCart = cartService.addItemToCart(cartId, itemDTO);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/{cartId}/check-inventory")
    public ResponseEntity<?> checkInventory(@PathVariable Long cartId) {
        try {
            Map<String, Object> result = cartService.checkInventoryForCart(cartId);
            if ((boolean) result.get("allAvailable")) {
                return ResponseEntity.ok().body(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        try {
            Cart updatedCart = cartService.removeItemFromCart(cartId, productId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItemQuantity(@PathVariable Long cartId, @PathVariable Long productId, @RequestBody CartItemRequestDTO itemDTO) {
        try {
            Cart updatedCart = cartService.updateItemQuanity(cartId, productId, itemDTO.quantity);
            return ResponseEntity.ok(updatedCart);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Cart> getCartById(@PathVariable Long id) {
        Cart cart = cartService.findById(id);
        return cart != null ? ResponseEntity.ok(cart) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<Cart> findAll() {
        return cartService.findAll();
    }

    @PutMapping("/{id}")
    public Cart update(@PathVariable Long id, @RequestBody Cart cart) {
        return cartService.update(id, cart);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = cartService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<?> emptyCart(@PathVariable Long cartId) {
        try {
            Cart updatedCart = cartService.emptyCart(cartId);
            return ResponseEntity.ok(updatedCart);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping("/calculate")
    public ResponseEntity<CartCalculationResponseDTO> calculateCart(@RequestBody CartCalculationRequestDTO request) {
        CartCalculationResponseDTO response;
        if (request.isRushDelivery()) {
            response = cartService.calculateRushCartTotals(request);
        } else {
            response = cartService.calculateCartTotals(request);
        }
        return ResponseEntity.ok(response);
    }
}
