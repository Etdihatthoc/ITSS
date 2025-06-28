package com.hust.ict.aims.service.impl;

import com.hust.ict.aims.dto.*;
import com.hust.ict.aims.model.Cart;
import com.hust.ict.aims.model.CartItem;
import com.hust.ict.aims.model.Product;
import com.hust.ict.aims.repository.CartItemRepository;
import com.hust.ict.aims.repository.CartRepository;
import com.hust.ict.aims.repository.ProductRepository;
import com.hust.ict.aims.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    public CartCalculationResponseDTO calculateCartTotals(CartCalculationRequestDTO request) {
        CartCalculationResponseDTO response = new CartCalculationResponseDTO();
        List<CartItemDetailDTO> itemDetails = new ArrayList<>();
        List<InsufficientStockDTO> outOfStockItems = new ArrayList<>();
        boolean allItemsAvailable = true;
        double subtotal = 0;
        double heaviestItemWeight = 0;

        // Process each item
        for (CartItemDTO item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));


            // Calculate item subtotal
            double itemSubtotal = product.getCurrentPrice() * item.getQuantity();
            subtotal += itemSubtotal;

            // Track heaviest item weight for shipping calculation
            if (product.getWeight() > heaviestItemWeight) {
                heaviestItemWeight = product.getWeight();
            }

            // Add to item details
            CartItemDetailDTO itemDetail = new CartItemDetailDTO();
            itemDetail.setProductId(product.getId());
            itemDetail.setTitle(product.getTitle());
            itemDetail.setPrice(product.getCurrentPrice());
            itemDetail.setQuantity(item.getQuantity());
            itemDetail.setSubtotal(itemSubtotal);
            itemDetail.setImageURL(product.getImageURL());
            itemDetail.setCategory(product.getCategory());
            itemDetail.setWeight(product.getWeight());

            itemDetails.add(itemDetail);
        }

        // Calculate VAT (10%)
        double tax = subtotal * 0.1;

        // Calculate delivery fee based on new rules
        double deliveryFee = calculateDeliveryFee(subtotal, request.isRushDelivery(),
                request.getProvince(), heaviestItemWeight);

        // Calculate total
        double total = subtotal + tax + deliveryFee;

        // Set response values
        response.setSubtotal(subtotal);
        response.setTax(tax);
        response.setDeliveryFee(deliveryFee);
        response.setTotal(total);
        response.setItems(itemDetails);
        response.setAllItemsAvailable(allItemsAvailable);
        response.setOutOfStockItems(outOfStockItems);

        return response;
    }

    @Override
    public CartCalculationResponseDTO calculateRushCartTotals(CartCalculationRequestDTO request) {
        CartCalculationResponseDTO response = new CartCalculationResponseDTO();
        boolean isAllItemsAvailable = true;
        double subtotal = 0;
        double normalSubtotal = 0;
        double rushSubtotal = 0;
        double normalHeaviestItemWeight = 0;
        double rushHeaviestItemWeight = 0;
        int rushItemCount = 0;
        List<InsufficientStockDTO> outOfStockItems = new ArrayList<>();
        List<CartItemDetailDTO> itemDetails = new ArrayList<>();
        Map<Long, Integer> items = new HashMap<>();

        for(CartItemDTO item : request.getItems()) {
            items.put(item.getProductId(), item.getQuantity());
        }

        List<Product> products = productRepository.findByIdIn(items.keySet());

        for(Product product: products) {
            long productId = product.getId();

            if(!items.containsKey(productId)) {
                throw new RuntimeException("Product not found: " + product.getId());
            }

            if(product.getQuantity() < items.get(productId)) {
                isAllItemsAvailable = false;
                InsufficientStockDTO outOfStockItem = new InsufficientStockDTO(productId, product.getTitle(), items.get(productId), product.getQuantity());
                outOfStockItems.add(outOfStockItem);
                continue;
            }

            double basePrice = product.getCurrentPrice();
            double adjustedPrice = product.isRushOrderEligible() ? basePrice + 10000 : basePrice;
            double itemSubtotal = adjustedPrice * items.get(productId);

            CartItemDetailDTO itemDetail = new CartItemDetailDTO();
            itemDetail.setProductId(productId);
            itemDetail.setTitle(product.getTitle());
            itemDetail.setPrice(adjustedPrice);
            itemDetail.setQuantity(items.get(productId));
            itemDetail.setSubtotal(itemSubtotal);
            itemDetail.setImageURL(product.getImageURL());
            itemDetail.setCategory(product.getCategory());
            itemDetail.setWeight(product.getWeight());

            itemDetails.add(itemDetail);

            if(product.isRushOrderEligible()) {
                rushSubtotal += itemSubtotal;
                rushItemCount++;
                rushHeaviestItemWeight = Math.max(rushHeaviestItemWeight, product.getWeight());
            } else {
                normalSubtotal += itemSubtotal;
                normalHeaviestItemWeight = Math.max(normalHeaviestItemWeight, product.getWeight());
            }
        }
        subtotal = normalSubtotal + rushSubtotal;

        double tax = subtotal * 0.1;

        double normalDeliveryFee = normalSubtotal > 0 ? calculateDeliveryFee(normalSubtotal, false, request.getProvince(), normalHeaviestItemWeight) : 0;
        double rushDeliveryFee = rushItemCount > 0 ? calculateDeliveryFee(rushSubtotal, true, request.getProvince(), rushHeaviestItemWeight) : 0;

        double total = subtotal + tax + rushDeliveryFee;

        response.setItems(itemDetails);
        response.setSubtotal(subtotal);
        response.setTax(tax);
        response.setDeliveryFee(normalDeliveryFee);
        response.setRushDeliveryFee(rushDeliveryFee);
        response.setTotal(total);
        response.setAllItemsAvailable(isAllItemsAvailable);
        response.setOutOfStockItems(outOfStockItems);

        return response;
    }

    /**
     * Calculates delivery fee based on location, order value, item weight, and rush delivery status
     *
     * @param subtotal Total value of non-rush items
     * @param isRushDelivery Whether this is a rush delivery
     * @param province Customer province/city
     * @param heaviestItemWeight Weight of the heaviest item in kg
     * @return The calculated delivery fee
     */
    private double calculateDeliveryFee(double subtotal, boolean isRushDelivery,
                                        String province, double heaviestItemWeight) {
        // Base variables
        double deliveryFee = 0;
        boolean isInnerCity = isInnerCityLocation(province);

        // Calculate base shipping fee based on location and weight
        if (isInnerCity) {
            // Inner Hanoi or HCMC: 22,000 VND for first 3kg
            deliveryFee = 22000;

            // Add 2,500 VND for each additional 0.5kg beyond 3kg
            if (heaviestItemWeight > 3.0) {
                double additionalWeight = heaviestItemWeight - 3.0;
                int additionalHalfKilos = (int) Math.ceil(additionalWeight / 0.5);
                deliveryFee += additionalHalfKilos * 2500;
            }
        } else {
            // Elsewhere in Vietnam: 30,000 VND for first 0.5kg
            deliveryFee = 30000;

            // Add 2,500 VND for each additional 0.5kg beyond 0.5kg
            if (heaviestItemWeight > 0.5) {
                double additionalWeight = heaviestItemWeight - 0.5;
                int additionalHalfKilos = (int) Math.ceil(additionalWeight / 0.5);
                deliveryFee += additionalHalfKilos * 2500;
            }
        }

        // Apply free shipping for orders over 100,000 VND (non-rush items only)
        if (!isRushDelivery && subtotal >= 100000) {
            // Free shipping up to 25,000 VND max
            if (deliveryFee <= 25000) {
                return 0; // Completely free
            } else {
                // Discount 25,000 VND from the delivery fee
                return deliveryFee - 25000;
            }
        }

        return deliveryFee;
    }

    /**
     * Determines if a location is in inner Hanoi or HCMC
     */
    private boolean isInnerCityLocation(String province) {
        if (province == null) {
            return false;
        }

        String normalizedProvince = province.toLowerCase().trim();

        // Check for inner Hanoi districts
        boolean isInnerHanoi = normalizedProvince.contains("hanoi") ||
                normalizedProvince.contains("hà nội");

        // Check for inner HCMC districts
        boolean isInnerHCMC = normalizedProvince.contains("ho chi minh") ||
                normalizedProvince.contains("hồ chí minh") ||
                normalizedProvince.contains("hcmc") ||
                normalizedProvince.contains("saigon") ||
                normalizedProvince.contains("sài gòn");

        return isInnerHanoi || isInnerHCMC;

        // Note: For a real implementation, you might want to check for specific inner districts
        // rather than just the city name. This would require a more detailed database of
        // districts and their classifications.
    }

    @Override
    @Transactional
    public Cart createCartSnapshot(CartRequestDTO cartData) {
        // Create new cart
        Cart cart = new Cart();
        cart.setTotalProductPriceBeforeVAT(cartData.getTotalProductPriceBeforeVAT());

        // Save cart first to get ID
        Cart savedCart = cartRepository.save(cart);

        // Create cart items
        List<CartItem> cartItems = new ArrayList<>();

        if (cartData.getItems() != null) {
            for (CartItemDTO itemRequest : cartData.getItems()) {
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
