package com.hust.ict.aims.model;
import jakarta.persistence.*;


// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All fields and methods directly relate to modeling invoice state and values.
// No formatting, logging, or unrelated behavior exists in this class.
//
// SRP COMMENT:
// Single Responsibility: This class strictly represents invoice data.
// It does not contain business logic such as price computation, discount policies, or output formatting.
// ------------------------------------------------------------

@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoiceid")
    private int id;

    @ManyToOne
    @JoinColumn(name = "cartid", referencedColumnName = "cartid", nullable = false)
    private Cart cart;

    @Column(name = "totalproductpriceaftervat", nullable = false)
    private float totalProductPriceAfterVAT;

    @Column(name = "totalamount", nullable = false)
    private float totalAmount;

    @Column(name = "deliveryfee", nullable = false)
    private float deliveryFee;

    public Invoice() {}

    public Invoice(Cart cart, float totalProductPriceAfterVAT, float totalAmount, float deliveryFee) {
        this.cart = cart;
        this.totalProductPriceAfterVAT = totalProductPriceAfterVAT;
        this.totalAmount = totalAmount;
        this.deliveryFee = deliveryFee;
    }

    public int getId() {
        return id;
    }

    public Cart getCart() {
        return cart;
    }

    public float getTotalProductPriceAfterVAT() {
        return totalProductPriceAfterVAT;
    }

    public float getTotalAmount() {
        return totalAmount;
    }

    public float getDeliveryFee() {
        return deliveryFee;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setTotalProductPriceAfterVAT(float totalProductPriceAfterVAT) {
        this.totalProductPriceAfterVAT = totalProductPriceAfterVAT;
    }

    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setDeliveryFee(float deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
}
