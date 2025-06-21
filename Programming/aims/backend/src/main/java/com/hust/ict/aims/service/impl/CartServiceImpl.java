package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.CartItemRequest;
import com.hust.ict.aims.dto.CartItemRequestDTO;
import com.hust.ict.aims.dto.CartRequest;
import com.hust.ict.aims.dto.InsufficientStockDTO;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.model.CartItem;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.repository.CartItemRepository;
import com.hust.ict.aims.repository.CartRepository;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Cart createCartSnapshot(CartRequest cartData) {
        // Create new cart
        Cart cart = new Cart();
        cart.setTotalProductPriceBeforeVAT(cartData.getTotalProductPriceBeforeVAT());

        // Save cart first to get ID
        Cart savedCart = cartRepository.save(cart);

        // Create cart items
        List<CartItem> cartItems = new ArrayList<>();

        if (cartData.getItems() != null) {
            for (CartItemRequest itemRequest : cartData.getItems()) {
                // Find product by ID
                Optional<Product> productOpt = productRepository.findById(itemRequest.getProductId());

                if (productOpt.isPresent()) {
                    Product product = productOpt.get();

                    // Create cart item
                    CartItem cartItem = new CartItem();
                    cartItem.setProduct(product);
                    cartItem.setQuantity(itemRequest.getQuantity());
                    cartItem.setCart(savedCart);

                    // Save cart item
                    CartItem savedItem = cartItemRepository.save(cartItem);
                    cartItems.add(savedItem);
                } else {
                    // Log warning if product not found
                    System.out.println("Warning: Product with ID " + itemRequest.getProductId() + " not found");
                }
            }
        }

        // Set items on cart
        savedCart.setItems(cartItems);

        return savedCart;
    }


    @Transactional
    public Cart emptyCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cart.getItems().clear();
        cart.setTotalProductPriceBeforeVAT(0);

        return cartRepository.save(cart);
    }

    @Autowired
    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Cart createEmptyCart() {
        Cart cart = new Cart();
        cart.setTotalProductPriceBeforeVAT(0);
        return cartRepository.save(cart);
    }

    @Transactional
    public Cart updateItemQuanity(Long cartId, Long productId, Integer newQuantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        CartItem cartItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        Product product = cartItem.getProduct();
        int currentItemQuantity = cartItem.getQuantity();
        int inventory = product.getQuantity();

        int quantityDelta = newQuantity - currentItemQuantity;

        if(quantityDelta > 0 && quantityDelta > inventory) {
            throw new IllegalArgumentException("Insufficient stock for product '" +
                    product.getTitle() + "'. Requested: " + newQuantity + ", Available: " + inventory);
        }

        if (newQuantity == 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(newQuantity);
        }

        float updatedTotal = (float) cart.getItems().stream()
                        .mapToDouble(i -> i.getProduct().getCurrentPrice() * i.getQuantity())
                        .sum();

        cart.setTotalProductPriceBeforeVAT(updatedTotal);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart addItemToCart(Long cartId, CartItemRequestDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Product product = productRepository.findById(dto.productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int requestedQuantity = dto.quantity;
        int available = product.getQuantity();

        // Check if the product is already in the cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        int totalRequestedQuantity = requestedQuantity;
        if (existingItem != null) {
            totalRequestedQuantity += existingItem.getQuantity();
        }


        // Check Validity
        if (available < requestedQuantity) {
            throw new IllegalArgumentException("Insufficient stock for product '" +
                    product.getTitle() + "'. Requested: " + requestedQuantity + ", Available: " + available);
        }

        // Add item to cart
        if (existingItem != null) {
            existingItem.setQuantity(totalRequestedQuantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(requestedQuantity);
            cart.addItem(newItem);
        }


        // Recalculate total
        float updatedTotal = (float) cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getCurrentPrice() * i.getQuantity())
                .sum();
        cart.setTotalProductPriceBeforeVAT(updatedTotal);

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Find the item in the cart
        CartItem itemToRemove = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        // Remove item from cart
        cart.getItems().remove(itemToRemove);

        // Recalculate cart total
        float updatedTotal = (float) cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getCurrentPrice() * i.getQuantity())
                .sum();
        cart.setTotalProductPriceBeforeVAT(updatedTotal);

        return cartRepository.save(cart);
    }

    public Map<String, Object> checkInventoryForCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<Map<String, Object>> issues = new ArrayList<>();
        boolean allAvailable = true;

        for (CartItem item : cart.getItems()) {
            Product product = item.getProduct();
            int available = product.getQuantity();
            int requested = item.getQuantity();

            if (available < requested) {
                allAvailable = false;
                Map<String, Object> issue = new HashMap<>();
                issue.put("productId", product.getId());
                issue.put("title", product.getTitle());
                issue.put("available", available);
                issue.put("requested", requested);
                issue.put("message", "Insufficient stock");
                issues.add(issue);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("allAvailable", allAvailable);
        if (!allAvailable) {
            response.put("outOfStockProducts", issues);
        }
        return response;
    }


    @Override
    public float calculateTotalBeforeVAT(Cart cart) {
        return (float) cart.getItems().stream()
                .mapToDouble(i -> i.getProduct().getCurrentPrice() * i.getQuantity())
                .sum();
    }

    @Override
    public boolean hasSufficientInventory(Cart cart) {
        return cart.getItems().stream()
                .allMatch(item -> item.getProduct().getQuantity() >= item.getQuantity());
    }

    @Override
    public List<CartItem> getInsufficientItems(Cart cart) {
        return cart.getItems().stream()
                .filter(item -> item.getProduct().getQuantity() < item.getQuantity())
                .collect(Collectors.toList());
    }

    @Override
    public float calculateTotalPriceIncludingVAT(Cart cart, float vatRate) {
        return cart.getTotalProductPriceBeforeVAT() * cart.getTotalProductPriceBeforeVAT() * (vatRate);
    }

    @Override
    public boolean isEligibleForFreeShipping(Cart cart) {
        return cart.getTotalProductPriceBeforeVAT() >= 100000;
    }

    @Override
    public float calculateShippingDiscount(Cart cart, float deliveryFee) {
        if (isEligibleForFreeShipping(cart)) {
            return deliveryFee > 25000 ? 2500 : deliveryFee;
        }
        return 0;
    }

    @Override
    public float calculateFinalAmount(Cart cart, float vatRate, float deliveryFee) {
        float totalWithVAT = calculateTotalPriceIncludingVAT(cart, vatRate);
        float shippingDiscount = calculateShippingDiscount(cart, deliveryFee);
        return totalWithVAT + (deliveryFee - shippingDiscount);
    }

    @Override
    public Cart save(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Cart findById(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    @Override
    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    @Override
    public Cart update(Long id, Cart cart) {
        if (!cartRepository.existsById(id)) {
            return null;
        }
        cart.setId(id);
        return cartRepository.save(cart);
    }

    @Override
    public boolean delete(Long id) {
        if (!cartRepository.existsById(id)) return false;
        cartRepository.deleteById(id);
        return true;
    }
}
