package com.hust.ict.aims.model;

import jakarta.persistence.*;


// ------------------------------------------------------------
// COHESION COMMENT:
// Functional cohesion: All fields and methods are directly related to representing the structure of an order.
// No utility, formatting, or unrelated domain logic is present.
//
// SRP COMMENT:
// Single Responsibility: This class is responsible for modeling the data of an order.
// It does not manage how the order is placed, validated, or processed — that belongs to service layer logic.
// ------------------------------------------------------------

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderid")
    private Long id;

    @OneToOne
    @JoinColumn(name = "transactionid", nullable = false, unique = true)
    private Transaction transaction;

    @OneToOne
    @JoinColumn(name = "invoiceid", nullable = false, unique = true)
    private Invoice invoice;

    @OneToOne
    @JoinColumn(name = "deliveryid", nullable = false, unique = true)
    private DeliveryInfo deliveryInfo;

    @Column(name = "orderstatus", nullable = false)
    private String status;


    public Orders() {}

    public Orders(Transaction transaction, Invoice invoice, DeliveryInfo deliveryInfo, String status) {
        this.transaction = transaction;
        this.invoice = invoice;
        this.deliveryInfo = deliveryInfo;
        this.status = status;
    }


    public Long getId() {
        return id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public String getStatus() {
        return status;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
