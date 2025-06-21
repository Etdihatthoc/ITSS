package com.hust.ict.aims.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Objects;

// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: Every field and method directly supports the purpose of modeling a cart item,
// i.e., linking a product with quantity inside a cart. No unrelated logic or responsibilities.
//
// SRP COMMENT:
// Single Responsibility: This class only represents the association between a cart and a product
// with a specified quantity. It does not compute prices or interact with other systems.
// ------------------------------------------------------------

@Entity
@Table(name = "cartitem")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartitemid")
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cartid", nullable = false)
    @JsonBackReference
    private Cart cart;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}
