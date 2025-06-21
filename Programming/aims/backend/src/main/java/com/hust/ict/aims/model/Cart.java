package com.hust.ict.aims.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

    // ------------------------------------------------------------
    // Functional cohesion:
    // High cohesion: all fields and methods relate to managing cart state and item collection.
    // No unrelated logic like formatting, pricing policy, or external dependency logic.
    //
    // SRP COMMENT:
    // Single Responsibility: Cart only manages the items it contains and tracks their total price.
    // It doesn't handle payment, taxes, or delivery.
    // ------------------------------------------------------------

    @Entity
    public class Cart {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "cartid")
        private Long cartId;

        @Column(name = "totalproductpricebeforevat", nullable = false)
        private float totalProductPriceBeforeVAT;

        @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        private List<CartItem> items = new ArrayList<CartItem>();

        // Getters and Setters

        public Long getCartId() {
            return cartId;
        }

        public void setCartId(Long id) {
            this.cartId = id;
        }

        public float getTotalProductPriceBeforeVAT() {
            return totalProductPriceBeforeVAT;
        }

        public void setTotalProductPriceBeforeVAT(float totalProductPriceBeforeVAT) {
            this.totalProductPriceBeforeVAT = totalProductPriceBeforeVAT;
        }

        public List<CartItem> getItems() {
            return items;
        }

        public void setItems(List<CartItem> items) {
            this.items = items;
        }

        public void setId(Long id) {
            this.cartId = id;
        }

        // Add/remove item logic
        public void addItem(CartItem item) {
            if (items == null) {
                items = new ArrayList<>();
            }
            items.add(item);
            item.setCart(this);
        }

        public void removeItem(CartItem item) {
            if (items != null) {
                items.remove(item);
                item.setCart(null);
            }
        }

            private void recalculateTotal() {
                float total = (float) items.stream()
                        .mapToDouble(ci -> ci.getProduct().getCurrentPrice() * ci.getQuantity())
                        .sum();
                this.totalProductPriceBeforeVAT = total;
            }
    }
